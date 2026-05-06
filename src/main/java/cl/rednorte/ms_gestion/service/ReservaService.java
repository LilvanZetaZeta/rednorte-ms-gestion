package cl.rednorte.ms_gestion.service;

import cl.rednorte.ms_gestion.client.NotificacionClient;
import cl.rednorte.ms_gestion.dto.NotificacionReservaRequest;
import cl.rednorte.ms_gestion.dto.ReservaRequest;
import cl.rednorte.ms_gestion.entity.CentroMedico;
import cl.rednorte.ms_gestion.entity.Reserva;
import cl.rednorte.ms_gestion.entity.Usuario;
import cl.rednorte.ms_gestion.repository.CentroMedicoRepository;
import cl.rednorte.ms_gestion.repository.ReservaRepository;
import cl.rednorte.ms_gestion.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservaService {

    @Autowired private ReservaRepository reservaRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CentroMedicoRepository centroMedicoRepository;
    @Autowired private NotificacionClient notificacionClient;

    public Reserva crear(ReservaRequest req) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String idAuthActual = authentication.getName();
        
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRATIVO") || 
                               a.getAuthority().equals("ROLE_DIRECTOR"));

        Usuario paciente;
        if (esAdmin && req.getPacienteId() != null) {
            paciente = usuarioRepository.findById(req.getPacienteId())
                    .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        } else {
            paciente = usuarioRepository.findByIdAuth(idAuthActual)
                    .orElseThrow(() -> new RuntimeException("Perfil de paciente no encontrado"));
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
        reserva.setOrigen(req.getOrigen());
        reserva.setEstado(Reserva.EstadoReserva.VIGENTE);
        
        Reserva reservaGuardada = reservaRepository.save(reserva);

        try {
            NotificacionReservaRequest notifReq = new NotificacionReservaRequest();
            notifReq.setPacienteId(paciente.getId()); // Aquí enviamos el ID numérico (ej: 3)
            notifReq.setCorreoDestino(paciente.getCorreo());
            notifReq.setNombrePaciente(paciente.getNombreCompleto());
            notifReq.setNombreMedico(medico.getNombreCompleto());
            notifReq.setNombreCentro(centro.getNombreSucursal());
            notifReq.setFechaHoraReserva(reservaGuardada.getFechaHora().toString());

            notificacionClient.notificarReserva(notifReq);
        } catch (Exception e) {
            System.err.println("Reserva guardada ID: " + reservaGuardada.getId() + " pero falló la notificación: " + e.getMessage());
        }

        return reservaGuardada;
    }

    public Reserva cancelar(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        reserva.setEstado(Reserva.EstadoReserva.CANCELADA);
        return reservaRepository.save(reserva);
    }

    public List<Reserva> findByPaciente(String idAuth) {
        return reservaRepository.findByPaciente_IdAuth(idAuth);
    }

    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }

    public Reserva obtenerReservaPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
    }

    public List<Reserva> obtenerReservasPorCentro(Long centroId) {
        return reservaRepository.findByCentroId(centroId);
    }

    public List<Reserva> obtenerReservasPorMedico(Long medicoId) {
        return reservaRepository.findByMedicoId(medicoId);
    }
}