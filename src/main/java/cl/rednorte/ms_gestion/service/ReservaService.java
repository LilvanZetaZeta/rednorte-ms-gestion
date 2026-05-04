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
        CentroMedico centro = centroMedicoRepository.findById(req.getCentroId())
                .orElseThrow(() -> new RuntimeException("Centro médico no encontrado"));

        Reserva reserva = new Reserva();
        reserva.setPaciente(paciente);
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

    public List<Reserva> findByPaciente(Long pacienteId) {
        return reservaRepository.findByPacienteId(pacienteId);
    }

    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }
}
