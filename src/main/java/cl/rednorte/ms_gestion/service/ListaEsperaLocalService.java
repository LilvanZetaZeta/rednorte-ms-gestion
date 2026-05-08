package cl.rednorte.ms_gestion.service;

import cl.rednorte.ms_gestion.dto.ListaEsperaRequest;
import cl.rednorte.ms_gestion.entity.ListaEsperaLocal;
import cl.rednorte.ms_gestion.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ListaEsperaLocalService {
    @Autowired private ListaEsperaLocalRepository repository;
    @Autowired private CentroMedicoRepository centroRepo;
    @Autowired private UsuarioRepository usuarioRepo;

    public List<ListaEsperaLocal> listarTodas() { return repository.findAll(); }
    public ListaEsperaLocal obtenerPorId(Long id) { return repository.findById(id).orElseThrow(() -> new RuntimeException("Registro en lista de espera no encontrado")); }
    public List<ListaEsperaLocal> obtenerPorPaciente(Long pacienteId) { return repository.findByPacienteId(pacienteId); }
    public List<ListaEsperaLocal> obtenerPorCentro(Long centroId) { return repository.findByCentroIdOrderByPrioridadAsc(centroId); }

    public ListaEsperaLocal crear(ListaEsperaRequest req) {
        ListaEsperaLocal l = new ListaEsperaLocal();
        l.setCentro(centroRepo.findById(req.getCentroId()).orElseThrow(() -> new RuntimeException("Centro médico no encontrado")));
        l.setPaciente(usuarioRepo.findById(req.getPacienteId()).orElseThrow(() -> new RuntimeException("Paciente no encontrado")));
        l.setPrioridad(req.getPrioridad());
        return repository.save(l);
    }

    public ListaEsperaLocal actualizar(Long id, ListaEsperaRequest req) {
        ListaEsperaLocal l = obtenerPorId(id);
        l.setPrioridad(req.getPrioridad());
        return repository.save(l);
    }

    public void eliminar(Long id) { repository.deleteById(id); }
}