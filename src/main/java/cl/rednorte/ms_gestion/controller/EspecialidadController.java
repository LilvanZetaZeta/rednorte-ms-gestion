package cl.rednorte.ms_gestion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.rednorte.ms_gestion.entity.Especialidad;
import cl.rednorte.ms_gestion.service.EspecialidadService;

@RestController
@RequestMapping("/api/gestion/especialidades")
public class EspecialidadController {
    @Autowired private EspecialidadService service;

    @PostMapping 
    public Especialidad create(@RequestBody Especialidad e) { 
      return service.crear(e); 
    }

    @PutMapping("/{id}") 
    public ResponseEntity<Especialidad> update(@PathVariable Long id, @RequestBody Especialidad e) { 
      return ResponseEntity.ok(service.actualizar(id, e)); 
    }

    @DeleteMapping("/{id}") 
    public ResponseEntity<Void> delete(@PathVariable Long id) { 
      service.eliminar(id); 
      return ResponseEntity.noContent().build(); 
    }
}