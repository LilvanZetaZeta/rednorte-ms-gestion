package cl.rednorte.ms_gestion.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import cl.rednorte.ms_gestion.client.NotificacionClient;
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

    @Autowired private ReservaRepository reservaRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CentroMedicoRepository centroMedicoRepository;
    @Autowired private NotificacionClient notificacionClient;

    public Reserva crear(ReservaRequest req) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String idAuthActual = auth.getName();

        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a ->
                    a.getAuthority().equals("ROLE_ADMINISTRATIVO") ||
                    a.getAuthority().equals("ROLE_DIRECTOR")
                );

        Usuario paciente = (req.getPacienteId() != null)
                ? usuarioRepository.findById(req.getPacienteId()).orElseThrow(() -> new RuntimeException("Paciente no encontrado"))
                : usuarioRepository.findByIdAuth(idAuthActual).orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        Usuario medico = usuarioRepository.findById(req.getMedicoId()).orElseThrow(() -> new RuntimeException("Médico no encontrado"));
        CentroMedico centro = centroMedicoRepository.findById(req.getCentroId()).orElseThrow(() -> new RuntimeException("Centro médico no encontrado"));

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
            System.err.println("[ms-gestion] Fallo notificación (no crítico): " + e.getMessage());
        }

        return guardada;
    }

    public Reserva actualizarTotal(Long id, ReservaRequest req) {
        Reserva r = obtenerPorId(id);
        r.setCentro(centroMedicoRepository.findById(req.getCentroId()).orElseThrow(() -> new RuntimeException("Centro médico no encontrado")));
        r.setMedico(usuarioRepository.findById(req.getMedicoId()).orElseThrow(() -> new RuntimeException("Médico no encontrado")));
        r.setFechaHora(req.getFechaHora());
        return reservaRepository.save(r);
    }

    public Reserva parchear(Long id, Map<String, Object> updates) {
        Reserva r = obtenerPorId(id);
        if (updates.containsKey("estado")) {
            r.setEstado(Reserva.EstadoReserva.valueOf((String) updates.get("estado")));
        }
        return reservaRepository.save(r);
    }
   
    public Reserva cancelar(Long id) {
        Reserva reserva = obtenerPorId(id);
        reserva.setEstado(Reserva.EstadoReserva.CANCELADA);
        return reservaRepository.save(reserva);
    }
 
    public void eliminar(Long id) {
        reservaRepository.deleteById(id);
    }

    private Reserva obtenerPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada: id=" + id));
    }
}