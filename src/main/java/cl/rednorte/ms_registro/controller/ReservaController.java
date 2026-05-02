package cl.rednorte.ms_registro.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.rednorte.ms_registro.dto.ReservaRequestDTO;
import cl.rednorte.ms_registro.dto.ReservaResponseDTO;
import cl.rednorte.ms_registro.enums.EstadoReservaEnum;
import cl.rednorte.ms_registro.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    // Leer todas las reservas del sistema (Para administradores)
    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(reservaService.listarTodas());
    }

    // Leer solo las reservas de un paciente (Pantalla "Mis Reservas")
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<ReservaResponseDTO>> listarPorPaciente(@PathVariable UUID pacienteId) {
        return ResponseEntity.ok(reservaService.listarPorPaciente(pacienteId));
    }

    // Crear nueva reserva
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crearReserva(@Valid @RequestBody ReservaRequestDTO dto) {
        return new ResponseEntity<>(reservaService.crearReserva(dto), HttpStatus.CREATED);
    }

    // --- PROTECCIÓN DE ARQUITECTURA ---
    // Al agregar el Regex en la ruta, garantizamos que "id" sea estrictamente un UUID válido.
    // Evita choques de rutas y previene ataques de inyección en la URL.
    
    // Obtener una reserva específica
    @GetMapping("/{id:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}")
    public ResponseEntity<ReservaResponseDTO> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    // Actualizar reserva
    @PutMapping("/{id:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}")
    public ResponseEntity<ReservaResponseDTO> actualizarReserva(
            @PathVariable UUID id,
            @Valid @RequestBody ReservaRequestDTO dto,
            @RequestParam EstadoReservaEnum nuevoEstado) {
        return ResponseEntity.ok(reservaService.actualizarReserva(id, dto, nuevoEstado));
    }
}