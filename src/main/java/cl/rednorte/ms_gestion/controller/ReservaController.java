package cl.rednorte.ms_gestion.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.rednorte.ms_gestion.dto.ReservaRequest;
import cl.rednorte.ms_gestion.entity.Reserva;
import cl.rednorte.ms_gestion.service.ReservaService;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired private ReservaService reservaService;

    @GetMapping
    public List<Reserva> listar() {
        return reservaService.findAll();
    }

    @GetMapping("/paciente/{pacienteId}")
    public List<Reserva> porPaciente(@PathVariable Long pacienteId) {
        return reservaService.findByPaciente(pacienteId);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ReservaRequest req) {
        try {
            return ResponseEntity.ok(reservaService.crear(req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(reservaService.cancelar(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
