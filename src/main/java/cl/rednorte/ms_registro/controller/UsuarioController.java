package cl.rednorte.ms_registro.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.rednorte.ms_registro.dto.UsuarioRequestDTO;
import cl.rednorte.ms_registro.dto.UsuarioResponseDTO;
import cl.rednorte.ms_registro.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // --- LEER TODOS ---
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    // --- LEER POR RUT ---
    @GetMapping("/rut/{rut}")
    public ResponseEntity<UsuarioResponseDTO> obtenerUsuarioPorRut(@PathVariable String rut) {
        return ResponseEntity.ok(usuarioService.obtenerPorRut(rut));
    }

    // --- CREAR ---
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> registrarUsuario(@Valid @RequestBody UsuarioRequestDTO dto) {
        return new ResponseEntity<>(usuarioService.registrarUsuario(dto), HttpStatus.CREATED);
    }

    // --- ACTUALIZAR COMPLETO (PUT) ---
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(
            @PathVariable UUID id, 
            @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, dto));
    }

    // --- ACTUALIZAR PARCIAL (PATCH) ---
    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarParcial(
            @PathVariable UUID id, 
            @RequestBody UsuarioRequestDTO dto) { 
        // Nota: Quitamos @Valid porque en PATCH no todos los campos son obligatorios
        return ResponseEntity.ok(usuarioService.actualizarParcial(id, dto));
    }

    // --- ELIMINAR (DELETE) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable UUID id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build(); // Retorna un código 204 (Eliminado con éxito, sin contenido que mostrar)
    }
}