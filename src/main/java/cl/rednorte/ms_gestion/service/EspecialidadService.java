package cl.rednorte.ms_gestion.service;

import cl.rednorte.ms_gestion.entity.Especialidad;
import cl.rednorte.ms_gestion.repository.EspecialidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EspecialidadService {
    @Autowired private EspecialidadRepository repository;

    public List<Especialidad> listar() { return repository.findAll(); }
    public Especialidad obtenerPorId(Long id) { return repository.findById(id).orElseThrow(); }
    public Especialidad crear(Especialidad e) { return repository.save(e); }
    public Especialidad actualizar(Long id, Especialidad req) {
        return repository.findById(id).map(e -> {
            e.setNombre(req.getNombre());
            return repository.save(e);
        }).orElseThrow();
    }
    public void eliminar(Long id) { repository.deleteById(id); }
}