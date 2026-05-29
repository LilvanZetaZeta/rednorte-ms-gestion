package cl.rednorte.ms_gestion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cl.rednorte.ms_gestion.dto.HistorialCitaRequest;
import cl.rednorte.ms_gestion.entity.HistorialCita;
import cl.rednorte.ms_gestion.service.HistorialCitaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/gestion/historial-citas")
public class HistorialCitaController {

    @Autowired private HistorialCitaService service;

    @PostMapping
    public ResponseEntity<HistorialCita> crear(@Valid @RequestBody HistorialCitaRequest req) {
        return ResponseEntity.ok(service.crearRegistro(req));
    }
}