package cl.rednorte.ms_registro.service;

import java.util.List;

import org.springframework.stereotype.Service;
import java.util.UUID;
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
    // Método para LEER por RUT
    public Usuario obtenerPorRut(String rut) {
        return usuarioRepository.findByRut(rut)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró ningún usuario con el RUT: " + rut));
    }

    // Método para ACTUALIZAR
    public Usuario actualizarUsuario(UUID id, Usuario datosActualizados) {
        // 1. Buscamos si el usuario existe
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));

        // 2. Validamos que si está cambiando el correo, no use uno que ya le pertenece a otra persona
        if (!usuarioExistente.getCorreo().equals(datosActualizados.getCorreo()) && 
            usuarioRepository.existsByCorreo(datosActualizados.getCorreo())) {
            throw new IllegalArgumentException("El nuevo correo ya está en uso por otro usuario.");
        }

        // 3. Actualizamos los datos permitidos
        usuarioExistente.setNombreCompleto(datosActualizados.getNombreCompleto());
        usuarioExistente.setCorreo(datosActualizados.getCorreo());
        
        // 4. Guardamos los cambios
        return usuarioRepository.save(usuarioExistente);
    }
}