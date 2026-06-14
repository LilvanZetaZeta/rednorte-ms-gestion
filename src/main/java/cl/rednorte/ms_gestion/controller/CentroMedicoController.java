package cl.rednorte.ms_gestion.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.rednorte.ms_gestion.entity.CentroMedico;
import cl.rednorte.ms_gestion.service.CentroMedicoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/gestion/centros-medicos")
public class CentroMedicoController {

    @Autowired private CentroMedicoService service;

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