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

import cl.rednorte.ms_gestion.dto.ListaEsperaRequest;
import cl.rednorte.ms_gestion.entity.ListaEsperaLocal;
import cl.rednorte.ms_gestion.service.ListaEsperaLocalService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/lista-espera")
public class ListaEsperaController {
    @Autowired private ListaEsperaLocalService service;

    @GetMapping public List<ListaEsperaLocal> getAll() { 
        return service.listarTodas(); 
    }

    @GetMapping("/{id}") public ResponseEntity<ListaEsperaLocal> getById(@PathVariable Long id) { 
        return ResponseEntity.ok(service.obtenerPorId(id)); 
    }

    @GetMapping("/centro/{centroId}") public List<ListaEsperaLocal> porCentro(@PathVariable Long centroId) { 
        return service.obtenerPorCentro(centroId); 
    }

    @GetMapping("/paciente/{pacienteId}") public List<ListaEsperaLocal> porPaciente(@PathVariable Long pacienteId) { 
        return service.obtenerPorPaciente(pacienteId); 
    }

    @PostMapping public ResponseEntity<ListaEsperaLocal> create(@Valid @RequestBody ListaEsperaRequest req) { 
        return ResponseEntity.ok(service.crear(req)); 
    }

    @PutMapping("/{id}") public ResponseEntity<ListaEsperaLocal> update(@PathVariable Long id, @Valid @RequestBody ListaEsperaRequest req) { 
        return ResponseEntity.ok(service.actualizar(id, req)); 
    }

    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { 
        service.eliminar(id); return ResponseEntity.noContent().build(); 
    }
}