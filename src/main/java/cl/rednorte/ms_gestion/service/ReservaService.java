package cl.rednorte.ms_gestion.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.rednorte.ms_gestion.client.CupoLiberadoClient;
import cl.rednorte.ms_gestion.client.NotificacionClient;
import cl.rednorte.ms_gestion.dto.CupoLiberadoEvent;
import cl.rednorte.ms_gestion.dto.NotificacionReservaRequest;
import cl.rednorte.ms_gestion.dto.ReservaRequest;
import cl.rednorte.ms_gestion.entity.CentroMedico;
import cl.rednorte.ms_gestion.entity.Reserva;
import cl.rednorte.ms_gestion.entity.Usuario;
import cl.rednorte.ms_gestion.repository.CentroMedicoRepository;
import cl.rednorte.ms_gestion.repository.ReservaRepository;
import cl.rednorte.ms_gestion.repository.UsuarioRepository;

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

        long horasFaltantes = ChronoUnit.HOURS.between(LocalDateTime.now(), reserva.getFechaHora());

        if (horasFaltantes < 24) {
            if (isPaciente) {
                throw new RuntimeException("No puede cancelar su cita con menos de 24 horas de anticipación.");
            } else if (isSecretaria) {
                reserva.setEstado(Reserva.EstadoReserva.PENDIENTE_CANCELACION_ADMIN);
                return reservaRepository.save(reserva);
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
}