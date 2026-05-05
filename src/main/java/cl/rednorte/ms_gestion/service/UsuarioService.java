package cl.rednorte.ms_gestion.service;

import cl.rednorte.ms_gestion.dto.RegistroRequest;
import cl.rednorte.ms_gestion.entity.Usuario;
import cl.rednorte.ms_gestion.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario registrarPerfilPaciente(RegistroRequest req) {
        if (usuarioRepository.existsByCorreo(req.getCorreo()))
            throw new RuntimeException("El correo ya está registrado en el sistema médico.");
        if (usuarioRepository.existsByRut(req.getRut()))
            throw new RuntimeException("El RUT ya está registrado en el sistema médico.");

        Usuario usuario = new Usuario();
        usuario.setIdAuth(req.getIdAuth()); 
        usuario.setRut(req.getRut());
        usuario.setNombreCompleto(req.getNombreCompleto());
        usuario.setCorreo(req.getCorreo());
        usuario.setRol(req.getRol() != null ? req.getRol() : Usuario.RolUsuario.PACIENTE);

        return usuarioRepository.save(usuario);
    }

    public Usuario obtenerPorIdAuth(String idAuth) {
        return usuarioRepository.findByIdAuth(idAuth)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con idAuth: " + idAuth));
    }

    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setNombreCompleto(usuarioActualizado.getNombreCompleto());
            usuario.setCorreo(usuarioActualizado.getCorreo());
            return usuarioRepository.save(usuario);
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}