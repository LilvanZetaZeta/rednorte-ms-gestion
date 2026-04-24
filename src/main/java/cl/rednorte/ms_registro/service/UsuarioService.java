package cl.rednorte.ms_registro.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.rednorte.ms_registro.entity.Usuario;
import cl.rednorte.ms_registro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // Método para LEER
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // Método para CREAR con Reglas de Negocio
    public Usuario registrarUsuario(Usuario nuevoUsuario) {
        
        // 1. Verificamos si el RUT ya existe
        if (usuarioRepository.existsByRut(nuevoUsuario.getRut())) {
            throw new IllegalArgumentException("El RUT ingresado ya se encuentra registrado.");
        }
        
        // 2. Verificamos si el correo ya existe
        if (usuarioRepository.existsByCorreo(nuevoUsuario.getCorreo())) {
            throw new IllegalArgumentException("El correo ingresado ya se encuentra registrado.");
        }
        
        // Si pasa las validaciones, lo guardamos
        return usuarioRepository.save(nuevoUsuario);
    }
}