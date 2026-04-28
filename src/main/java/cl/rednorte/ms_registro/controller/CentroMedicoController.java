package cl.rednorte.ms_registro.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.rednorte.ms_registro.dto.CentroMedicoRequestDTO;
import cl.rednorte.ms_registro.dto.CentroMedicoResponseDTO;
import cl.rednorte.ms_registro.service.CentroMedicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/centros-medicos")
@RequiredArgsConstructor
public class CentroMedicoController {

    private final CentroMedicoService centroMedicoService;

    // --- LEER TODOS ---
    @GetMapping
    public ResponseEntity<List<CentroMedicoResponseDTO>> listarCentros() {
        return ResponseEntity.ok(centroMedicoService.listarTodos());
    }

    // --- LEER POR ID (NUEVO) ---
    @GetMapping("/{id}")
    public ResponseEntity<CentroMedicoResponseDTO> obtenerCentroPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(centroMedicoService.obtenerPorId(id));
    }

    // --- CREAR ---
    @PostMapping
    public ResponseEntity<CentroMedicoResponseDTO> registrarCentro(@Valid @RequestBody CentroMedicoRequestDTO dto) {
        return new ResponseEntity<>(centroMedicoService.registrarCentro(dto), HttpStatus.CREATED);
    }

    // --- ACTUALIZAR (NUEVO) ---
    @PutMapping("/{id}")
    public ResponseEntity<CentroMedicoResponseDTO> actualizarCentro(
            @PathVariable UUID id, 
            @Valid @RequestBody CentroMedicoRequestDTO dto) {
        return ResponseEntity.ok(centroMedicoService.actualizarCentro(id, dto));
    }

    // --- ELIMINAR ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCentro(@PathVariable UUID id) {
        centroMedicoService.eliminarCentro(id);
        return ResponseEntity.noContent().build();
    }
}