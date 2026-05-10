package cl.rednorte.ms_gestion.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.rednorte.ms_gestion.dto.RegistroRequest;
import cl.rednorte.ms_gestion.entity.CentroMedico;
import cl.rednorte.ms_gestion.entity.Especialidad;
import cl.rednorte.ms_gestion.entity.Usuario;
import cl.rednorte.ms_gestion.repository.CentroMedicoRepository;
import cl.rednorte.ms_gestion.repository.EspecialidadRepository;
import cl.rednorte.ms_gestion.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private EspecialidadRepository especialidadRepository;

    public List<Usuario> listarTodos() { return usuarioRepository.findAll(); }
    public Usuario obtenerPorId(Long id) { return usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado")); }
    public Usuario obtenerPorIdAuth(String idAuth) { return usuarioRepository.findByIdAuth(idAuth).orElseThrow(() -> new RuntimeException("Usuario no encontrado")); }

    public List<Usuario> buscarMedicosPorEspecialidad(String especialidad) {
        return usuarioRepository.findByRolAndEspecialidades_NombreIgnoreCase(Usuario.RolUsuario.MEDICO, especialidad);
    }

    public Usuario registrarPerfilPaciente(RegistroRequest req) {
        if (usuarioRepository.existsByCorreo(req.getCorreo())) throw new RuntimeException("Correo registrado");
        if (usuarioRepository.existsByRut(req.getRut())) throw new RuntimeException("RUT registrado");

        Usuario u = new Usuario();
        u.setIdAuth(req.getIdAuth()); 
        u.setRut(req.getRut());
        u.setNombreCompleto(req.getNombreCompleto());
        u.setCorreo(req.getCorreo());
        u.setRol(req.getRol() != null ? req.getRol() : Usuario.RolUsuario.PACIENTE);

        if (u.getRol() == Usuario.RolUsuario.MEDICO && req.getEspecialidadIds() != null) {
            List<Especialidad> especialidades = especialidadRepository.findAllById(req.getEspecialidadIds());
            u.setEspecialidades(especialidades);
        }

        return usuarioRepository.save(u);
    }

    public Usuario actualizarUsuario(Long id, Usuario req) {
        return usuarioRepository.findById(id).map(u -> {
            u.setNombreCompleto(req.getNombreCompleto());
            u.setCorreo(req.getCorreo());
            if (req.getEspecialidades() != null) u.setEspecialidades(req.getEspecialidades());
            return usuarioRepository.save(u);
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Usuario parchearUsuario(Long id, Map<String, Object> updates) {
        Usuario u = obtenerPorId(id);
        updates.forEach((k, v) -> {
            if (k.equals("nombreCompleto")) u.setNombreCompleto((String) v);
            if (k.equals("correo")) u.setCorreo((String) v);
        });
        return usuarioRepository.save(u);
    }

    public Usuario asignarRolMedicoPorCorreo(String correo) {
        Usuario u = usuarioRepository.findByCorreo(correo)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el correo: " + correo));
        
        u.setRol(Usuario.RolUsuario.MEDICO);
        return usuarioRepository.save(u);
    }

    @Autowired private CentroMedicoRepository centroMedicoRepository;

    public List<Usuario> listarPersonalStaff() {
        return usuarioRepository.findByRolNot(Usuario.RolUsuario.PACIENTE);
    }

    public Usuario actualizarRol(Long id, Usuario.RolUsuario nuevoRol) {
        Usuario u = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        u.setRol(nuevoRol);
        return usuarioRepository.save(u);
    }

    public Usuario actualizarCentroMedico(Long usuarioId, Long centroId) {
        Usuario u = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (centroId == null) {
            u.setCentroMedico(null);
        } else {
            CentroMedico cm = centroMedicoRepository.findById(centroId).orElseThrow(() -> new RuntimeException("Centro médico no encontrado"));
            u.setCentroMedico(cm);
        }
        return usuarioRepository.save(u);
    }

    public void eliminarUsuario(Long id) { usuarioRepository.deleteById(id); }
}