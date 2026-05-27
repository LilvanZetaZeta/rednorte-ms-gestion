package cl.rednorte.ms_gestion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cl.rednorte.ms_gestion.entity.HistorialCita;
import cl.rednorte.ms_gestion.entity.Usuario;
import cl.rednorte.ms_gestion.dto.HistorialCitaRequest;
import cl.rednorte.ms_gestion.repository.HistorialCitaRepository;
import cl.rednorte.ms_gestion.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@Service
public class HistorialCitaService {

    @Autowired private HistorialCitaRepository historialRepo;
    @Autowired private UsuarioRepository usuarioRepo;

    @Transactional
    public HistorialCita crearRegistro(HistorialCitaRequest req) {
        Usuario paciente = usuarioRepo.findById(req.getPacienteId())
            .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));
            
        Usuario medico = usuarioRepo.findById(req.getMedicoId())
            .orElseThrow(() -> new EntityNotFoundException("Médico no encontrado"));

        HistorialCita historial = new HistorialCita();
        historial.setPaciente(paciente);
        historial.setMedico(medico);
        historial.setFechaAtencion(LocalDateTime.now());
        historial.setObservaciones(req.getObservaciones());
        historial.setProcedimientoRealizado(req.getProcedimientoRealizado());

        return historialRepo.save(historial);
    }
}