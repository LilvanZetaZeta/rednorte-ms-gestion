package cl.rednorte.ms_gestion.controller;

import cl.rednorte.ms_gestion.entity.Especialidad;
import cl.rednorte.ms_gestion.service.EspecialidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadController {
    @Autowired private EspecialidadService service;

    @GetMapping 
    public List<Especialidad> getAll() { 
      return service.listar(); 
    }

    @GetMapping("/{id}") 
    public ResponseEntity<Especialidad> getById(@PathVariable Long id) { 
      return ResponseEntity.ok(service.obtenerPorId(id)); 
    }

    @PostMapping public Especialidad create(@RequestBody Especialidad e) { 
      return service.crear(e); 
    }

    @PutMapping("/{id}") public ResponseEntity<Especialidad> update(@PathVariable Long id, @RequestBody Especialidad e) { 
      return ResponseEntity.ok(service.actualizar(id, e)); 
    }

    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { 
      service.eliminar(id); return ResponseEntity.noContent().build(); 
    }
}