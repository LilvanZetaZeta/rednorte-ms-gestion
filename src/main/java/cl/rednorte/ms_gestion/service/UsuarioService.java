package cl.rednorte.ms_gestion.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.rednorte.ms_gestion.dto.RegistroRequest;
import cl.rednorte.ms_gestion.entity.CentroMedico;
import cl.rednorte.ms_gestion.entity.Especialidad;
import cl.rednorte.ms_gestion.entity.Usuario;
import cl.rednorte.ms_gestion.repository.CentroMedicoRepository;
import cl.rednorte.ms_gestion.repository.EspecialidadRepository;
import cl.rednorte.ms_gestion.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class UsuarioService {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private EspecialidadRepository especialidadRepository;
    @Autowired private CentroMedicoRepository centroMedicoRepository;

    // Métodos de lectura interna (necesarios para que los updates funcionen)
    public Usuario obtenerPorId(Long id) { 
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id)); 
    }

    @Transactional
    public Usuario registrarPerfilPaciente(RegistroRequest req) {
        if (usuarioRepository.existsByCorreo(req.getCorreo())) {
            throw new IllegalArgumentException("El correo electrónico ya se encuentra registrado.");
        }
        if (usuarioRepository.existsByRut(req.getRut())) {
            throw new IllegalArgumentException("El RUT ya se encuentra registrado.");
        }

        Usuario u = new Usuario();
        u.setIdAuth(req.getIdAuth()); 
        u.setRut(req.getRut());
        u.setNombreCompleto(req.getNombreCompleto());
        u.setCorreo(req.getCorreo());
        u.setRol(req.getRol() != null ? req.getRol() : Usuario.RolUsuario.PACIENTE);

        if (u.getRol() == Usuario.RolUsuario.MEDICO && req.getEspecialidadIds() != null && !req.getEspecialidadIds().isEmpty()) {
            List<Especialidad> especialidades = especialidadRepository.findAllById(req.getEspecialidadIds());
            if (especialidades.isEmpty()) {
                throw new IllegalArgumentException("Las especialidades proporcionadas no existen.");
            }
            u.setEspecialidades(especialidades);
        }

        return usuarioRepository.save(u);
    }

    @Transactional
    public Usuario actualizarUsuario(Long id, Usuario req) {
        Usuario u = obtenerPorId(id);

        // Validar que si cambia el correo, el nuevo no esté ocupado
        if (!u.getCorreo().equalsIgnoreCase(req.getCorreo()) && usuarioRepository.existsByCorreo(req.getCorreo())) {
            throw new IllegalArgumentException("El nuevo correo ya está en uso por otro usuario.");
        }

        u.setNombreCompleto(req.getNombreCompleto());
        u.setCorreo(req.getCorreo());
        if (req.getEspecialidades() != null) {
            u.setEspecialidades(req.getEspecialidades());
        }
        
        return usuarioRepository.save(u);
    }

    @Transactional
    public Usuario parchearUsuario(Long id, Map<String, Object> updates) {
        Usuario u = obtenerPorId(id);
        
        if (updates.containsKey("correo")) {
            String nuevoCorreo = (String) updates.get("correo");
            if (!u.getCorreo().equalsIgnoreCase(nuevoCorreo) && usuarioRepository.existsByCorreo(nuevoCorreo)) {
                throw new IllegalArgumentException("El nuevo correo ya está en uso por otro usuario.");
            }
            u.setCorreo(nuevoCorreo);
        }
        if (updates.containsKey("nombreCompleto")) {
            u.setNombreCompleto((String) updates.get("nombreCompleto"));
        }
        
        return usuarioRepository.save(u);
    }

    @Transactional
    public Usuario asignarRolMedicoPorCorreo(String correo) {
        Usuario u = usuarioRepository.findByCorreo(correo)
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con el correo: " + correo));
        
        if (u.getRol() == Usuario.RolUsuario.MEDICO) {
            throw new IllegalStateException("El usuario ya tiene el rol de Médico.");
        }
        
        u.setRol(Usuario.RolUsuario.MEDICO);
        return usuarioRepository.save(u);
    }

    @Transactional
    public Usuario asignarRolAdminPorCorreo(String correo) {
        Usuario u = usuarioRepository.findByCorreo(correo)
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con el correo: " + correo));
        
        if (u.getRol() == Usuario.RolUsuario.ADMINISTRATIVO) {
            throw new IllegalStateException("El usuario ya tiene el rol de Administrativo.");
        }
        
        u.setRol(Usuario.RolUsuario.ADMINISTRATIVO);
        return usuarioRepository.save(u);
    }

    @Transactional
    public Usuario actualizarRol(Long id, Usuario.RolUsuario nuevoRol) {
        Usuario u = obtenerPorId(id);
        u.setRol(nuevoRol);
        return usuarioRepository.save(u);
    }

    @Transactional
    public Usuario actualizarCentroMedico(Long usuarioId, Long centroId) {
        Usuario u = obtenerPorId(usuarioId);
        
        if (centroId == null) {
            u.setCentroMedico(null);
        } else {
            CentroMedico cm = centroMedicoRepository.findById(centroId)
                .orElseThrow(() -> new EntityNotFoundException("Centro médico no encontrado con ID: " + centroId));
            u.setCentroMedico(cm);
        }
        return usuarioRepository.save(u);
    }

    @Transactional
    public Usuario actualizarEspecialidades(Long usuarioId, List<Long> especialidadIds) {
        Usuario u = obtenerPorId(usuarioId);
        
        if (u.getRol() != Usuario.RolUsuario.MEDICO) {
            throw new IllegalStateException("Solo se pueden asignar especialidades a usuarios con rol de MÉDICO.");
        }
        
        if (especialidadIds == null || especialidadIds.isEmpty()) {
            u.setEspecialidades(List.of());
        } else {
            List<Especialidad> especialidades = especialidadRepository.findAllById(especialidadIds);
            if (especialidades.size() != especialidadIds.size()) {
                throw new IllegalArgumentException("Una o más especialidades proporcionadas no existen en el sistema.");
            }
            u.setEspecialidades(especialidades);
        }
        
        return usuarioRepository.save(u);
    }

    @Transactional
    public void eliminarUsuario(Long id) { 
        if (!usuarioRepository.existsById(id)) {
            throw new EntityNotFoundException("No se puede eliminar: Usuario no encontrado.");
        }
        usuarioRepository.deleteById(id); 
    }
}