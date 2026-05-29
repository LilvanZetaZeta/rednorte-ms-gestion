package cl.rednorte.ms_gestion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cl.rednorte.ms_gestion.dto.PerfilPacienteRequest;
import cl.rednorte.ms_gestion.entity.PerfilPaciente;
import cl.rednorte.ms_gestion.service.PerfilPacienteService;

@RestController
@RequestMapping("/api/gestion/perfil-pacientes")
public class PerfilPacienteController {

    @Autowired private PerfilPacienteService service;

    @PostMapping
    public ResponseEntity<PerfilPaciente> crear(@RequestBody PerfilPacienteRequest req) {
        return ResponseEntity.ok(service.crearPerfil(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerfilPaciente> actualizar(@PathVariable Long id, @RequestBody PerfilPacienteRequest req) {
        return ResponseEntity.ok(service.actualizarPerfil(id, req));
    }
}