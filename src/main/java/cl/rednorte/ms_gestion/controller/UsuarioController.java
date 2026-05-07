package cl.rednorte.ms_gestion.controller;

import cl.rednorte.ms_gestion.dto.RegistroRequest;
import cl.rednorte.ms_gestion.entity.Usuario;
import cl.rednorte.ms_gestion.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired private UsuarioService service;

    @GetMapping
    public List<Usuario> listar() { return service.listarTodos(); }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getById(@PathVariable Long id) { return ResponseEntity.ok(service.obtenerPorId(id)); }

    @GetMapping("/perfil/{idAuth}")
    public ResponseEntity<Usuario> getPerfil(@PathVariable String idAuth) { return ResponseEntity.ok(service.obtenerPorIdAuth(idAuth)); }

    @GetMapping("/medicos/buscar")
    public ResponseEntity<List<Usuario>> buscarMedicos(@RequestParam String especialidad) {
        return ResponseEntity.ok(service.buscarMedicosPorEspecialidad(especialidad));
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody RegistroRequest req) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarPerfilPaciente(req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @RequestBody Usuario u) { 
        return ResponseEntity.ok(service.actualizarUsuario(id, u)); 
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Usuario> parchear(@PathVariable Long id, @RequestBody Map<String, Object> updates) { 
        return ResponseEntity.ok(service.parchearUsuario(id, updates)); 
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}