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

import cl.rednorte.ms_registro.dto.ListaEsperaRequestDTO;
import cl.rednorte.ms_registro.dto.ListaEsperaResponseDTO;
import cl.rednorte.ms_registro.enums.EstadoListaEsperaEnum;
import cl.rednorte.ms_registro.service.ListaEsperaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lista-espera")
@RequiredArgsConstructor
public class ListaEsperaController {

    private final ListaEsperaService listaEsperaService;

    // --- 1. INGRESAR A LA FILA ---
    @PostMapping
    public ResponseEntity<ListaEsperaResponseDTO> ingresarALaFila(@Valid @RequestBody ListaEsperaRequestDTO dto) {
        return new ResponseEntity<>(listaEsperaService.ingresarALaFila(dto), HttpStatus.CREATED);
    }

    // --- 2. VER LA FILA ORDENADA (Por Centro Médico) ---
    @GetMapping("/centro/{centroId}")
    public ResponseEntity<List<ListaEsperaResponseDTO>> obtenerFilaPorCentro(@PathVariable UUID centroId) {
        return ResponseEntity.ok(listaEsperaService.obtenerFilaPorCentro(centroId));
    }

    // --- 3. ACTUALIZAR ESTADO (Avanzar en la fila) ---
    @PutMapping("/{id}/estado")
    public ResponseEntity<ListaEsperaResponseDTO> actualizarEstado(
            @PathVariable UUID id,
            @RequestParam EstadoListaEsperaEnum nuevoEstado) {
        return ResponseEntity.ok(listaEsperaService.actualizarEstado(id, nuevoEstado));
    }
}