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

    // ¡ELIMINADOS BCryptPasswordEncoder y JwtUtil! Supabase hace ese trabajo.

    public Usuario registrarPerfilPaciente(RegistroRequest req) {
        if (usuarioRepository.existsByCorreo(req.getCorreo()))
            throw new RuntimeException("El correo ya está registrado en el sistema médico.");
        if (usuarioRepository.existsByRut(req.getRut()))
            throw new RuntimeException("El RUT ya está registrado en el sistema médico.");

        Usuario usuario = new Usuario();
        // IMPORTANTE: Debes agregar este campo a tu Entity y DTO. 
        // Es el UUID que une tu tabla local con la cuenta real en Supabase.
        usuario.setIdAuth(req.getIdAuth()); 
        
        usuario.setRut(req.getRut());
        usuario.setNombreCompleto(req.getNombreCompleto());
        usuario.setCorreo(req.getCorreo());
        
        // La contraseña se elimina por completo de la Entidad y de la Base de Datos.
        usuario.setRol(req.getRol() != null ? req.getRol() : Usuario.RolUsuario.PACIENTE);

        return usuarioRepository.save(usuario);
    }

    // EL MÉTODO login() SE ELIMINA POR COMPLETO.
    // Tu backend ya no emite tokens ni valida contraseñas. El login ocurre 100% en React.
}