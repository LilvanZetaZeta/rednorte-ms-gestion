package cl.rednorte.ms_gestion.service;

import cl.rednorte.ms_gestion.JwtUtil;
import cl.rednorte.ms_gestion.dto.LoginRequest;
import cl.rednorte.ms_gestion.dto.LoginResponse;
import cl.rednorte.ms_gestion.dto.RegistroRequest;
import cl.rednorte.ms_gestion.entity.Usuario;
import cl.rednorte.ms_gestion.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public Usuario registro(RegistroRequest req) {
        if (usuarioRepository.existsByCorreo(req.getCorreo()))
            throw new RuntimeException("El correo ya está registrado");
        if (usuarioRepository.existsByRut(req.getRut()))
            throw new RuntimeException("El RUT ya está registrado");

        Usuario usuario = new Usuario();
        usuario.setRut(req.getRut());
        usuario.setNombreCompleto(req.getNombreCompleto());
        usuario.setCorreo(req.getCorreo());
        usuario.setContrasena(encoder.encode(req.getContrasena()));
        usuario.setRol(req.getRol() != null ? req.getRol() : Usuario.RolUsuario.PACIENTE);

        return usuarioRepository.save(usuario);
    }

    public LoginResponse login(LoginRequest req) {
        Usuario usuario = usuarioRepository.findByCorreo(req.getCorreo())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!encoder.matches(req.getContrasena(), usuario.getContrasena()))
            throw new RuntimeException("Credenciales inválidas");

        String token = jwtUtil.generateToken(usuario.getCorreo(), Map.of(
                "id", usuario.getId(),
                "rol", usuario.getRol().name(),
                "nombre", usuario.getNombreCompleto()
        ));

        return new LoginResponse(token, usuario.getId(), usuario.getCorreo(),
                usuario.getNombreCompleto(), usuario.getRol());
    }
}
