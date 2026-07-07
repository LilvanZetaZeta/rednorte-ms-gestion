package cl.rednorte.ms_gestion.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cl.rednorte.ms_gestion.client.CupoLiberadoClient;
import cl.rednorte.ms_gestion.client.NotificacionClient;
import cl.rednorte.ms_gestion.dto.BloqueoAgendaRequest;
import cl.rednorte.ms_gestion.dto.CupoLiberadoEvent;
import cl.rednorte.ms_gestion.dto.NotificacionReservaRequest;
import cl.rednorte.ms_gestion.dto.ReservaRequest;
import cl.rednorte.ms_gestion.entity.CentroMedico;
import cl.rednorte.ms_gestion.entity.Reserva;
import cl.rednorte.ms_gestion.entity.Usuario;
import cl.rednorte.ms_gestion.repository.CentroMedicoRepository;
import cl.rednorte.ms_gestion.repository.ReservaRepository;
import cl.rednorte.ms_gestion.repository.UsuarioRepository;
import java.time.LocalTime;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private CentroMedicoRepository centroMedicoRepository;
    @Autowired
    private NotificacionClient notificacionClient;
    @Autowired
    private CupoLiberadoClient cupoLiberadoClient;

    @Transactional
    public Reserva crear(ReservaRequest req) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String idAuthActual = auth.getName();

        Usuario usuarioActual = usuarioRepository.findByIdAuth(idAuthActual).orElse(null);

        boolean esSecretaria = usuarioActual != null && usuarioActual.getRol() == Usuario.RolUsuario.SECRETARIA;

        boolean esAdmin = usuarioActual != null && (
                usuarioActual.getRol() == Usuario.RolUsuario.ADMINISTRATIVO ||
                usuarioActual.getRol() == Usuario.RolUsuario.DIRECTOR ||
                usuarioActual.getRol() == Usuario.RolUsuario.SECRETARIA);

        if (esSecretaria) {
            Usuario secretaria = usuarioActual;
            if (secretaria.getCentroMedico() == null
                    || !secretaria.getCentroMedico().getId().equals(req.getCentroId())) {
                throw new RuntimeException(
                        "Operación inválida: No puedes crear reservas para una sucursal distinta a la tuya.");
            }
        }

        Usuario paciente;

        if (req.getPacienteId() != null) {
            paciente = usuarioRepository.findById(req.getPacienteId())
                    .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        } else if (req.getPacienteRut() != null && esAdmin) {
            paciente = usuarioRepository.findByRut(req.getPacienteRut()).orElseGet(() -> {
                if (req.getPacienteCorreo() == null || req.getPacienteNombreCompleto() == null) {
                    throw new RuntimeException(
                            "El RUT no existe. Se requiere correo y nombre para registrar al paciente.");
                }
                Usuario nuevoPaciente = new Usuario();
                nuevoPaciente.setRut(req.getPacienteRut());
                nuevoPaciente.setCorreo(req.getPacienteCorreo());
                nuevoPaciente.setNombreCompleto(req.getPacienteNombreCompleto());
                nuevoPaciente.setRol(Usuario.RolUsuario.PACIENTE);
                nuevoPaciente.setIdAuth(UUID.randomUUID().toString());
                return usuarioRepository.save(nuevoPaciente);
            });
        } else {
            paciente = usuarioRepository.findByIdAuth(idAuthActual)
                    .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        }

        Usuario medico = usuarioRepository.findById(req.getMedicoId())
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));
        CentroMedico centro = centroMedicoRepository.findById(req.getCentroId())
                .orElseThrow(() -> new RuntimeException("Centro médico no encontrado"));

        // Validar que no se reserve una cita en el pasado
        if (req.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No se puede reservar una cita en una fecha y hora pasada.");
        }

        // Validar que el slot no esté ya tomado por otro paciente
        reservaRepository.findConflicto(medico.getId(), req.getFechaHora().toString()).ifPresent(r -> {
            throw new RuntimeException("La hora seleccionada ya está reservada. Por favor elige otro horario disponible.");
        });

        // Validar que la hora sea un slot válido (08:00–19:40, cada 20 min)
        LocalTime hora = req.getFechaHora().toLocalTime();
        LocalTime inicio = java.time.LocalTime.of(8, 0);
        LocalTime limiteFin = java.time.LocalTime.of(19, 40);
        if (hora.isBefore(inicio) || hora.isAfter(limiteFin) || hora.getMinute() % 20 != 0) {
            throw new RuntimeException("La hora seleccionada no corresponde a un horario válido (08:00–20:00, cada 20 minutos).");
        }

        Reserva reserva = new Reserva();
        reserva.setPaciente(paciente);
        reserva.setMedico(medico);
        reserva.setCentro(centro);
        reserva.setFechaHora(req.getFechaHora());
        reserva.setTipoReserva(req.getTipoReserva());
        reserva.setOrigen(req.getOrigen());
        reserva.setEstado(Reserva.EstadoReserva.VIGENTE);

        Reserva guardada = reservaRepository.save(reserva);

        try {
            NotificacionReservaRequest notif = new NotificacionReservaRequest();
            notif.setPacienteId(paciente.getId());
            notif.setCorreoDestino(paciente.getCorreo());
            notif.setNombrePaciente(paciente.getNombreCompleto());
            notif.setNombreMedico(medico.getNombreCompleto());
            notif.setNombreCentro(centro.getNombreSucursal());
            notif.setFechaHoraReserva(guardada.getFechaHora().toString());
            notificacionClient.notificarReserva(notif);
        } catch (Exception e) {
            System.err.println("[ms-gestion] Fallo notificación (no crítico): " + e.getMessage());
        }

        return guardada;
    }

    public Reserva actualizarTotal(Long id, ReservaRequest req) {
        Reserva r = obtenerPorId(id);
        
        // Validar que no se actualice a una fecha y hora pasada
        if (req.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No se puede reservar una cita en una fecha y hora pasada.");
        }
        
        r.setCentro(centroMedicoRepository.findById(req.getCentroId())
                .orElseThrow(() -> new RuntimeException("Centro médico no encontrado")));
        r.setMedico(usuarioRepository.findById(req.getMedicoId())
                .orElseThrow(() -> new RuntimeException("Médico no encontrado")));
        r.setFechaHora(req.getFechaHora());
        return reservaRepository.save(r);
    }

    @Transactional
    public Reserva parchear(Long id, Map<String, Object> updates) {
        Reserva r = obtenerPorId(id);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioActual = usuarioRepository.findByIdAuth(auth.getName()).orElse(null);
        boolean esSecretaria = usuarioActual != null && usuarioActual.getRol() == Usuario.RolUsuario.SECRETARIA;

        if (esSecretaria) {
            Usuario secretaria = usuarioActual;
            if (secretaria.getCentroMedico() == null
                    || !secretaria.getCentroMedico().getId().equals(r.getCentro().getId())) {
                throw new RuntimeException(
                        "Acceso denegado: No tienes facultades operativas sobre las citas de otros recintos.");
            }
        }

        if (updates.containsKey("estado")) {
            r.setEstado(Reserva.EstadoReserva.valueOf((String) updates.get("estado")));
        }
        return reservaRepository.save(r);
    }

    @Transactional
    public Reserva cancelar(Long id) {
        Reserva reserva = obtenerPorId(id);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioActual = usuarioRepository.findByIdAuth(auth.getName()).orElse(null);
        boolean isSecretaria = usuarioActual != null && usuarioActual.getRol() == Usuario.RolUsuario.SECRETARIA;
        boolean isPaciente = usuarioActual != null && usuarioActual.getRol() == Usuario.RolUsuario.PACIENTE;

        LocalDateTime ahora = LocalDateTime.now();
        if (reserva.getFechaHora().isBefore(ahora)) {
            long horasTranscurridas = ChronoUnit.HOURS.between(reserva.getFechaHora(), ahora);
            if (horasTranscurridas < 24) {
                throw new RuntimeException("No se puede cancelar una cita que ya ha transcurrido hace menos de 24 horas.");
            }
        } else {
            long horasFaltantes = ChronoUnit.HOURS.between(ahora, reserva.getFechaHora());
            if (horasFaltantes < 24) {
                if (isPaciente) {
                    throw new RuntimeException("No puede cancelar su cita con menos de 24 horas de anticipación.");
                } else if (isSecretaria) {
                    reserva.setEstado(Reserva.EstadoReserva.PENDIENTE_CANCELACION_ADMIN);
                    return reservaRepository.save(reserva);
                }
            }
        }

        reserva.setEstado(Reserva.EstadoReserva.CANCELADA);
        Reserva guardada = reservaRepository.save(reserva);
        notificarCupoLiberadoAutonomo(guardada);
        return guardada;
    }

    @Async
    public void notificarCupoLiberadoAutonomo(Reserva reserva) {
        try {
            CupoLiberadoEvent event = new CupoLiberadoEvent();
            event.setReservaOriginalId(reserva.getId());
            event.setMedicoId(reserva.getMedico().getId());
            event.setCentroId(reserva.getCentro().getId());
            event.setFechaHora(reserva.getFechaHora());
            event.setEspecialidad(reserva.getMedico().getEspecialidades() != null
                    && !reserva.getMedico().getEspecialidades().isEmpty()
                            ? reserva.getMedico().getEspecialidades().get(0).getNombre()
                            : "GENERAL");
            event.setTipoProcedimiento(reserva.getTipoReserva().name());
            cupoLiberadoClient.notificarCupoLiberado(event);
        } catch (Exception e) {
            System.err.println("[ms-gestion] Fallo evento cupo liberado: " + e.getMessage());
        }
    }

    public void eliminar(Long id) {
        reservaRepository.deleteById(id);
    }

    private Reserva obtenerPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada: id=" + id));
    }

    @Transactional
    public void bloquearAgendaYProcesarCitas(BloqueoAgendaRequest req) {
        
        java.time.LocalDateTime inicioDia = req.getFechaBloqueo().atStartOfDay();
        java.time.LocalDateTime finDia = req.getFechaBloqueo().atTime(23, 59, 59);

        List<Reserva> reservasActivas = reservaRepository.findByMedicoIdAndFechaHoraBetweenAndEstadoIn(
            req.getMedicoId(), 
            inicioDia, 
            finDia, 
            List.of(Reserva.EstadoReserva.VIGENTE, Reserva.EstadoReserva.CONFIRMADA)
        );

        if (reservasActivas.isEmpty()) {
            return;
        }
        
        for (Reserva reserva : reservasActivas) {
            reserva.setEstado(Reserva.EstadoReserva.CANCELADA);
        }

        reservaRepository.saveAll(reservasActivas);
        
        System.out.println("Se han cancelado " + reservasActivas.size() + " citas del médico ID " + req.getMedicoId() + " para la fecha " + req.getFechaBloqueo());
    }
}