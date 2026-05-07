package cl.rednorte.ms_gestion.service;

import cl.rednorte.ms_gestion.client.NotificacionClient;
import cl.rednorte.ms_gestion.dto.NotificacionReservaRequest;
import cl.rednorte.ms_gestion.dto.ReservaRequest;
import cl.rednorte.ms_gestion.entity.*;
import cl.rednorte.ms_gestion.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class ReservaService {

    @Autowired private ReservaRepository reservaRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CentroMedicoRepository centroMedicoRepository;
    @Autowired private NotificacionClient notificacionClient;

    public List<Reserva> findAll() { return reservaRepository.findAll(); }
    public Reserva obtenerReservaPorId(Long id) { return reservaRepository.findById(id).orElseThrow(); }
    public List<Reserva> findByPaciente(String idAuth) { return reservaRepository.findByPaciente_IdAuth(idAuth); }
    public List<Reserva> obtenerReservasPorCentro(Long centroId) { return reservaRepository.findByCentroId(centroId); }
    public List<Reserva> obtenerReservasPorMedico(Long medicoId) { return reservaRepository.findByMedicoId(medicoId); }

    public Reserva crear(ReservaRequest req) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String idAuthActual = auth.getName();
        
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRATIVO") || a.getAuthority().equals("ROLE_DIRECTOR"));

        Usuario paciente = (esAdmin && req.getPacienteId() != null) ? 
            usuarioRepository.findById(req.getPacienteId()).orElseThrow() : 
            usuarioRepository.findByIdAuth(idAuthActual).orElseThrow();

        Usuario medico = usuarioRepository.findById(req.getMedicoId()).orElseThrow();
        CentroMedico centro = centroMedicoRepository.findById(req.getCentroId()).orElseThrow();

        Reserva reserva = new Reserva();
        reserva.setPaciente(paciente);
        reserva.setMedico(medico);
        reserva.setCentro(centro);
        reserva.setFechaHora(req.getFechaHora());
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
            System.err.println("Fallo notificación: " + e.getMessage());
        }

        return guardada;
    }

    public Reserva actualizarTotal(Long id, ReservaRequest req) {
        Reserva r = obtenerReservaPorId(id);
        r.setCentro(centroMedicoRepository.findById(req.getCentroId()).orElseThrow());
        r.setMedico(usuarioRepository.findById(req.getMedicoId()).orElseThrow());
        r.setFechaHora(req.getFechaHora());
        return reservaRepository.save(r);
    }

    public Reserva parchear(Long id, Map<String, Object> updates) {
        Reserva r = obtenerReservaPorId(id);
        if (updates.containsKey("estado")) {
            r.setEstado(Reserva.EstadoReserva.valueOf((String) updates.get("estado")));
        }
        return reservaRepository.save(r);
    }

    public Reserva cancelar(Long id) {
        Reserva reserva = obtenerReservaPorId(id);
        reserva.setEstado(Reserva.EstadoReserva.CANCELADA);
        return reservaRepository.save(reserva);
    }

    public void eliminar(Long id) { reservaRepository.deleteById(id); }
}