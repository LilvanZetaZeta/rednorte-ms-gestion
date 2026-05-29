package cl.rednorte.ms_gestion.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.rednorte.ms_gestion.dto.RegistroRequest;
import cl.rednorte.ms_gestion.entity.Usuario;
import cl.rednorte.ms_gestion.service.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/gestion/usuarios")
public class UsuarioController {

    @Autowired private UsuarioService service;

    @PostMapping
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroRequest req) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarPerfilPaciente(req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @Valid @RequestBody Usuario u) { 
        return ResponseEntity.ok(service.actualizarUsuario(id, u)); 
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Usuario> parchear(@PathVariable Long id, @RequestBody Map<String, Object> updates) { 
        return ResponseEntity.ok(service.parchearUsuario(id, updates)); 
    }

    @PostMapping("/asignar-medico")
    public ResponseEntity<?> asignarMedico(@RequestBody Map<String, String> request) {
        try {
            String correo = request.get("correo");
            return ResponseEntity.ok(service.asignarRolMedicoPorCorreo(correo));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/asignar-admin")
    public ResponseEntity<?> asignarAdmin(@RequestBody Map<String, String> request) {
        try {
            String correo = request.get("correo");
            return ResponseEntity.ok(service.asignarRolAdminPorCorreo(correo));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/rol")
    public ResponseEntity<Usuario> actualizarRol(@PathVariable Long id, @RequestBody Map<String, String> request) {
        Usuario.RolUsuario nuevoRol = Usuario.RolUsuario.valueOf(request.get("rol").toUpperCase());
        return ResponseEntity.ok(service.actualizarRol(id, nuevoRol));
    }

    @PatchMapping("/{id}/centro")
    public ResponseEntity<Usuario> actualizarCentro(@PathVariable Long id, @RequestBody Map<String, Long> request) {
        Long centroId = request.get("centroId");
        return ResponseEntity.ok(service.actualizarCentroMedico(id, centroId));
    }
    
    @PatchMapping("/{id}/especialidades")
    public ResponseEntity<Usuario> actualizarEspecialidades(@PathVariable Long id, @RequestBody List<Long> especialidadIds) {
        return ResponseEntity.ok(service.actualizarEspecialidades(id, especialidadIds));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}