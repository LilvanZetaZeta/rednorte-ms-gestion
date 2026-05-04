package cl.rednorte.ms_gestion.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.rednorte.ms_gestion.dto.ListaEsperaRequest;
import cl.rednorte.ms_gestion.entity.CentroMedico;
import cl.rednorte.ms_gestion.entity.ListaEsperaLocal;
import cl.rednorte.ms_gestion.entity.Usuario;
import cl.rednorte.ms_gestion.repository.CentroMedicoRepository;
import cl.rednorte.ms_gestion.repository.ListaEsperaLocalRepository;
import cl.rednorte.ms_gestion.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/lista-espera")
public class ListaEsperaController {

    @Autowired private ListaEsperaLocalRepository listaRepository;
    @Autowired private CentroMedicoRepository centroRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    @GetMapping("/centro/{centroId}")
    public List<ListaEsperaLocal> porCentro(@PathVariable Long centroId) {
        return listaRepository.findByCentroIdOrderByPrioridadAsc(centroId);
    }

    @PostMapping
    public ResponseEntity<?> agregar(@RequestBody ListaEsperaRequest req) {
        try {
            CentroMedico centro = centroRepository.findById(req.getCentroId())
                    .orElseThrow(() -> new RuntimeException("Centro no encontrado"));
            Usuario paciente = usuarioRepository.findById(req.getPacienteId())
                    .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

            ListaEsperaLocal entrada = new ListaEsperaLocal();
            entrada.setCentro(centro);
            entrada.setPaciente(paciente);
            entrada.setPrioridad(req.getPrioridad() != null ? req.getPrioridad() : 0);
            return ResponseEntity.ok(listaRepository.save(entrada));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        listaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
