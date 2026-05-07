package cl.rednorte.ms_gestion.controller;

import cl.rednorte.ms_gestion.dto.ReservaRequest;
import cl.rednorte.ms_gestion.entity.Reserva;
import cl.rednorte.ms_gestion.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    @Autowired private ReservaService service;

    @GetMapping public List<Reserva> listar() { return service.findAll(); }
    @GetMapping("/{id}") public ResponseEntity<Reserva> getById(@PathVariable Long id) { return ResponseEntity.ok(service.obtenerReservaPorId(id)); }
    @GetMapping("/paciente/{pacienteId}") public List<Reserva> porPaciente(@PathVariable String pacienteId) { return service.findByPaciente(pacienteId); }
    @GetMapping("/medico/{medicoId}") public ResponseEntity<List<Reserva>> porMedico(@PathVariable Long medicoId) { return ResponseEntity.ok(service.obtenerReservasPorMedico(medicoId)); }
    @GetMapping("/centro/{centroId}") public ResponseEntity<List<Reserva>> porCentro(@PathVariable Long centroId) { return ResponseEntity.ok(service.obtenerReservasPorCentro(centroId)); }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ReservaRequest req) {
        try { return ResponseEntity.ok(service.crear(req)); } 
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reserva> actualizar(@PathVariable Long id, @RequestBody ReservaRequest req) {
        return ResponseEntity.ok(service.actualizarTotal(id, req));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Reserva> parchear(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(service.parchear(id, updates));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        try { return ResponseEntity.ok(service.cancelar(id)); } 
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}