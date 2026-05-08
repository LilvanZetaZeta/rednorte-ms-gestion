package cl.rednorte.ms_gestion.controller;

import cl.rednorte.ms_gestion.entity.CentroMedico;
import cl.rednorte.ms_gestion.service.CentroMedicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/centros-medicos")
public class CentroMedicoController {
    @Autowired private CentroMedicoService service;

    @GetMapping
    public List<CentroMedico> listar() { return service.listarTodos(); }

    @GetMapping("/buscar")
    public ResponseEntity<List<CentroMedico>> buscar(
            @RequestParam(required = false) String region, 
            @RequestParam(required = false) String comuna) {
        return ResponseEntity.ok(service.buscarPorLocalizacion(region, comuna));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CentroMedico> getById(@PathVariable Long id) { return ResponseEntity.ok(service.obtenerPorId(id)); }

    @PostMapping
    public CentroMedico crear(@Valid @RequestBody CentroMedico c) { return service.crearCentro(c); }

    @PutMapping("/{id}")
    public ResponseEntity<CentroMedico> actualizar(@PathVariable Long id, @Valid @RequestBody CentroMedico c) {
        return ResponseEntity.ok(service.actualizarCentro(id, c));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CentroMedico> parchear(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(service.parchearCentro(id, updates));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminarCentro(id);
        return ResponseEntity.noContent().build();
    }
}