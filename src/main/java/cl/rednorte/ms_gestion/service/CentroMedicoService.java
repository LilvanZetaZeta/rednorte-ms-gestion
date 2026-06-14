package cl.rednorte.ms_gestion.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.rednorte.ms_gestion.entity.CentroMedico;
import cl.rednorte.ms_gestion.repository.CentroMedicoRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class CentroMedicoService {

    @Autowired private CentroMedicoRepository repository;

    public CentroMedico obtenerPorId(Long id) { 
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Centro médico no encontrado con ID: " + id)); 
    }

    @Transactional
    public CentroMedico crearCentro(CentroMedico c) { 
        if (c.getNombreSucursal() == null || c.getNombreSucursal().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la sucursal es obligatorio.");
        }
        if (repository.findByNombreSucursalIgnoreCase(c.getNombreSucursal()).isPresent()) {
            throw new IllegalArgumentException("El centro médico '" + c.getNombreSucursal() + "' ya existe en el sistema.");
        }
        return repository.save(c); 
    }

    @Transactional
    public CentroMedico actualizarCentro(Long id, CentroMedico req) {
        CentroMedico c = obtenerPorId(id);
        
        if (req.getNombreSucursal() == null || req.getNombreSucursal().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la sucursal no puede estar vacío.");
        }

        repository.findByNombreSucursalIgnoreCase(req.getNombreSucursal()).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new IllegalArgumentException("El centro médico '" + req.getNombreSucursal() + "' ya existe en el sistema.");
            }
        });

        c.setNombreSucursal(req.getNombreSucursal());
        c.setRegion(req.getRegion());
        c.setComuna(req.getComuna());
        c.setDireccion(req.getDireccion());
        
        return repository.save(c);
    }

    @Transactional
    public CentroMedico parchearCentro(Long id, Map<String, Object> updates) {
        CentroMedico c = obtenerPorId(id);
        
        if (updates.containsKey("nombreSucursal")) {
            String v = (String) updates.get("nombreSucursal");
            if (v == null || v.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre no puede estar vacío.");
            }
            repository.findByNombreSucursalIgnoreCase(v).ifPresent(existente -> {
                if (!existente.getId().equals(id)) {
                    throw new IllegalArgumentException("El centro médico '" + v + "' ya existe en el sistema.");
                }
            });
            c.setNombreSucursal(v);
        }
        if (updates.containsKey("region")) {
            c.setRegion((String) updates.get("region"));
        }
        if (updates.containsKey("comuna")) {
            c.setComuna((String) updates.get("comuna"));
        }
        if (updates.containsKey("direccion")) {
            c.setDireccion((String) updates.get("direccion"));
        }

        return repository.save(c);
    }

    @Transactional
    public void eliminarCentro(Long id) { 
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("No se puede eliminar: Centro médico no encontrado.");
        }
        repository.deleteById(id); 
    }
}