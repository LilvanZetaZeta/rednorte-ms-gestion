package cl.rednorte.ms_gestion.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.rednorte.ms_gestion.dto.RegistroRequest;
import cl.rednorte.ms_gestion.entity.Usuario;
import cl.rednorte.ms_gestion.repository.UsuarioRepository;
import cl.rednorte.ms_gestion.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired 
    private UsuarioService usuarioService;
    
    @Autowired 
    private UsuarioRepository usuarioRepository;

    // --- 1. CREAR PERFIL (Llamado por React justo después de registrarse en Supabase) ---
    @PostMapping
    public ResponseEntity<?> registrarPerfil(@RequestBody RegistroRequest req) {
        try {
            // Llamamos al servicio que definimos en el paso anterior
            Usuario usuario = usuarioService.registrarPerfilPaciente(req);
            
            // Retornamos 201 Created
            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // --- ¡EL MÉTODO /login FUE ELIMINADO! ---

    // --- 2. LEER TODOS ---
    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        // Buena práctica: Retornar siempre un ResponseEntity
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    // --- 3. LEER POR ID LOCAL ---
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}