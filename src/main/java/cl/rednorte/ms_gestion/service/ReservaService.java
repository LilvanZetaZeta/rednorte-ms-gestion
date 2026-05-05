package cl.rednorte.ms_gestion.service;

import cl.rednorte.ms_gestion.dto.ReservaRequest;
import cl.rednorte.ms_gestion.entity.CentroMedico;
import cl.rednorte.ms_gestion.entity.Reserva;
import cl.rednorte.ms_gestion.entity.Usuario;
import cl.rednorte.ms_gestion.repository.CentroMedicoRepository;
import cl.rednorte.ms_gestion.repository.ReservaRepository;
import cl.rednorte.ms_gestion.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservaService {

    @Autowired private ReservaRepository reservaRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CentroMedicoRepository centroMedicoRepository;

    public Reserva crear(ReservaRequest req) {
        Usuario paciente = usuarioRepository.findById(req.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        
        // Buscamos al médico en la tabla usuarios
        Usuario medico = usuarioRepository.findById(req.getMedicoId())
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));

        CentroMedico centro = centroMedicoRepository.findById(req.getCentroId())
                .orElseThrow(() -> new RuntimeException("Centro médico no encontrado"));

        Reserva reserva = new Reserva();
        reserva.setPaciente(paciente);
        reserva.setMedico(medico); // Lo asignamos a la reserva
        reserva.setCentro(centro);
        reserva.setFechaHora(req.getFechaHora());
        reserva.setOrigen(req.getOrigen());
        reserva.setEstado(Reserva.EstadoReserva.VIGENTE);
        return reservaRepository.save(reserva);
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

    // ¡RESTAURADO!
    public List<Reserva> obtenerReservasPorMedico(Long medicoId) {
        return reservaRepository.findByMedicoId(medicoId);
    }
}