package cl.rednorte.ms_gestion.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.rednorte.ms_gestion.entity.Especialidad;
import cl.rednorte.ms_gestion.service.EspecialidadService;

@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadController {
    @Autowired private EspecialidadService service;

    @GetMapping 
    public List<Especialidad> getAll() { 
      return service.listar(); 
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