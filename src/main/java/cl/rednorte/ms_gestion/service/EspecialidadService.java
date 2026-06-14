package cl.rednorte.ms_gestion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.rednorte.ms_gestion.entity.Especialidad;
import cl.rednorte.ms_gestion.repository.EspecialidadRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class EspecialidadService {
    
    @Autowired private EspecialidadRepository repository;

    public Especialidad obtenerPorId(Long id) { 
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Especialidad no encontrada con ID: " + id)); 
    }

    @Transactional
    public Especialidad crear(Especialidad e) { 
        if (e.getNombre() == null || e.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la especialidad es obligatorio.");
        }
        if (repository.findByNombreIgnoreCase(e.getNombre()).isPresent()) {
            throw new IllegalArgumentException("La especialidad '" + e.getNombre() + "' ya existe en el sistema.");
        }
        return repository.save(e); 
    }

    @Transactional
    public Especialidad actualizar(Long id, Especialidad req) {
        Especialidad e = obtenerPorId(id);
        
        if (req.getNombre() == null || req.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la especialidad no puede estar vacío.");
        }
        
        // Verificar si el nuevo nombre ya está ocupado por otra especialidad
        repository.findByNombreIgnoreCase(req.getNombre()).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new IllegalArgumentException("La especialidad '" + req.getNombre() + "' ya existe en el sistema.");
            }
        });

        e.setNombre(req.getNombre());
        return repository.save(e);
    }

    @Transactional
    public void eliminar(Long id) { 
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("No se puede eliminar: Especialidad no encontrada.");
        }
        repository.deleteById(id); 
    }
}