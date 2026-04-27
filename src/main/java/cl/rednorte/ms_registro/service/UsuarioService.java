package cl.rednorte.ms_registro.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import cl.rednorte.ms_registro.dto.UsuarioRequestDTO;
import cl.rednorte.ms_registro.dto.UsuarioResponseDTO;
import cl.rednorte.ms_registro.entity.Usuario;
import cl.rednorte.ms_registro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // --- MÉTODO TRADUCTOR (De Entidad a DTO) ---
    private UsuarioResponseDTO mapearAResponse(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setRut(usuario.getRut());
        dto.setNombreCompleto(usuario.getNombreCompleto());
        dto.setCorreo(usuario.getCorreo());
        dto.setRol(usuario.getRol());
        dto.setCreadoEn(usuario.getCreadoEn());
        return dto;
    }

    // --- LEER ---
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO obtenerPorRut(String rut) {
        Usuario usuario = usuarioRepository.findByRut(rut)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró ningún usuario con el RUT: " + rut));
        return mapearAResponse(usuario);
    }

    // --- CREAR ---
    public UsuarioResponseDTO registrarUsuario(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByRut(dto.getRut())) {
            throw new IllegalArgumentException("El RUT ingresado ya se encuentra registrado.");
        }
        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new IllegalArgumentException("El correo ingresado ya se encuentra registrado.");
        }

        // Convertimos el DTO a Entidad para guardarlo
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setRut(dto.getRut());
        nuevoUsuario.setNombreCompleto(dto.getNombreCompleto());
        nuevoUsuario.setCorreo(dto.getCorreo());

        // Guardamos y devolvemos la versión traducida
        return mapearAResponse(usuarioRepository.save(nuevoUsuario));
    }

    // --- ACTUALIZAR COMPLETO (PUT) ---
    public UsuarioResponseDTO actualizarUsuario(UUID id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));

        if (!usuario.getCorreo().equals(dto.getCorreo()) && usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new IllegalArgumentException("El nuevo correo ya está en uso por otro usuario.");
        }

        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setCorreo(dto.getCorreo());
        
        return mapearAResponse(usuarioRepository.save(usuario));
    }

    // --- ACTUALIZAR PARCIAL (PATCH) ---
    public UsuarioResponseDTO actualizarParcial(UUID id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));

        // Solo actualiza si el campo viene con información
        if (dto.getNombreCompleto() != null && !dto.getNombreCompleto().isBlank()) {
            usuario.setNombreCompleto(dto.getNombreCompleto());
        }
        
        if (dto.getCorreo() != null && !dto.getCorreo().isBlank()) {
            if (!usuario.getCorreo().equals(dto.getCorreo()) && usuarioRepository.existsByCorreo(dto.getCorreo())) {
                throw new IllegalArgumentException("El nuevo correo ya está en uso por otro usuario.");
            }
            usuario.setCorreo(dto.getCorreo());
        }

        return mapearAResponse(usuarioRepository.save(usuario));
    }

    // --- ELIMINAR (DELETE) ---
    public void eliminarUsuario(UUID id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}