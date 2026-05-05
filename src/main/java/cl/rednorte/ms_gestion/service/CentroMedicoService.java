package cl.rednorte.ms_gestion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.rednorte.ms_gestion.entity.CentroMedico;
import cl.rednorte.ms_gestion.repository.CentroMedicoRepository;

@Service
public class CentroMedicoService {

    @Autowired
    private CentroMedicoRepository centroMedicoRepository;

    public CentroMedico actualizarCentro(Long id, CentroMedico centroActualizado) {
        return centroMedicoRepository.findById(id).map(centro -> {
            centro.setNombreSucursal(centroActualizado.getNombreSucursal());
            return centroMedicoRepository.save(centro);
        }).orElseThrow(() -> new RuntimeException("Centro Médico no encontrado"));
    }
  
    public void eliminarCentro(Long id) {
        if (!centroMedicoRepository.existsById(id)) {
            throw new RuntimeException("Centro Médico no encontrado");
        }
        centroMedicoRepository.deleteById(id);
    }
}