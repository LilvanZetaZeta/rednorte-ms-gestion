package cl.rednorte.ms_gestion.service;

import cl.rednorte.ms_gestion.entity.CentroMedico;
import cl.rednorte.ms_gestion.repository.CentroMedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class CentroMedicoService {

    @Autowired private CentroMedicoRepository repository;

    public List<CentroMedico> listarTodos() { return repository.findAll(); }
    public CentroMedico obtenerPorId(Long id) { return repository.findById(id).orElseThrow(); }

    public List<CentroMedico> buscarPorLocalizacion(String region, String comuna) {
        if (region != null && comuna != null) return repository.findByRegionAndComunaIgnoreCase(region, comuna);
        if (comuna != null) return repository.findByComunaIgnoreCase(comuna);
        return repository.findAll();
    }

    public CentroMedico crearCentro(CentroMedico c) { return repository.save(c); }

    public CentroMedico actualizarCentro(Long id, CentroMedico req) {
        return repository.findById(id).map(c -> {
            c.setNombreSucursal(req.getNombreSucursal());
            c.setRegion(req.getRegion());
            c.setComuna(req.getComuna());
            c.setDireccion(req.getDireccion());
            return repository.save(c);
        }).orElseThrow();
    }

    public CentroMedico parchearCentro(Long id, Map<String, Object> updates) {
        CentroMedico c = obtenerPorId(id);
        updates.forEach((k, v) -> {
            switch (k) {
                case "nombreSucursal" -> c.setNombreSucursal((String) v);
                case "region" -> c.setRegion((String) v);
                case "comuna" -> c.setComuna((String) v);
                case "direccion" -> c.setDireccion((String) v);
            }
        });
        return repository.save(c);
    }

    public void eliminarCentro(Long id) { repository.deleteById(id); }
}