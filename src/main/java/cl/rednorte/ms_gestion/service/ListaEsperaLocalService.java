package cl.rednorte.ms_gestion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.rednorte.ms_gestion.dto.ListaEsperaRequest;
import cl.rednorte.ms_gestion.entity.ListaEsperaLocal;
import cl.rednorte.ms_gestion.entity.CentroMedico;
import cl.rednorte.ms_gestion.entity.Usuario;
import cl.rednorte.ms_gestion.repository.CentroMedicoRepository;
import cl.rednorte.ms_gestion.repository.ListaEsperaLocalRepository;
import cl.rednorte.ms_gestion.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ListaEsperaLocalService {
    
    @Autowired private ListaEsperaLocalRepository repository;
    @Autowired private CentroMedicoRepository centroRepo;
    @Autowired private UsuarioRepository usuarioRepo;

    public ListaEsperaLocal obtenerPorId(Long id) { 
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Registro en lista de espera no encontrado con ID: " + id)); 
    }

    @Transactional
    public ListaEsperaLocal crear(ListaEsperaRequest req) {
        CentroMedico centro = centroRepo.findById(req.getCentroId())
            .orElseThrow(() -> new EntityNotFoundException("Centro médico no encontrado."));
        
        Usuario paciente = usuarioRepo.findById(req.getPacienteId())
            .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado."));

        // Validar que el paciente no esté ya en la lista de espera de este mismo centro
        List<ListaEsperaLocal> listasDelPaciente = repository.findByPacienteId(req.getPacienteId());
        boolean yaInscrito = listasDelPaciente.stream()
            .anyMatch(l -> l.getCentro().getId().equals(req.getCentroId()));
        
        if (yaInscrito) {
            throw new IllegalStateException("El paciente ya se encuentra en la lista de espera de este centro médico.");
        }

        if (req.getPrioridad() == null || req.getPrioridad() < 1) {
            throw new IllegalArgumentException("La prioridad debe ser un valor válido mayor a 0.");
        }

        ListaEsperaLocal l = new ListaEsperaLocal();
        l.setCentro(centro);
        l.setPaciente(paciente);
        l.setPrioridad(req.getPrioridad());
        
        return repository.save(l);
    }

    @Transactional
    public ListaEsperaLocal actualizar(Long id, ListaEsperaRequest req) {
        ListaEsperaLocal l = obtenerPorId(id);
        
        if (req.getPrioridad() == null || req.getPrioridad() < 1) {
            throw new IllegalArgumentException("La prioridad debe ser un valor válido mayor a 0.");
        }
        
        l.setPrioridad(req.getPrioridad());
        return repository.save(l);
    }

    @Transactional
    public void eliminar(Long id) { 
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("No se puede eliminar: Registro no encontrado.");
        }
        repository.deleteById(id); 
    }
}