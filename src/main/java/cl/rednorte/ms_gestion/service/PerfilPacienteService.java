package cl.rednorte.ms_gestion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.rednorte.ms_gestion.entity.PerfilPaciente;
import cl.rednorte.ms_gestion.entity.Usuario;
import cl.rednorte.ms_gestion.dto.PerfilPacienteRequest;
import cl.rednorte.ms_gestion.repository.PerfilPacienteRepository;
import cl.rednorte.ms_gestion.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class PerfilPacienteService {

    @Autowired
    private PerfilPacienteRepository perfilRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Transactional
    public PerfilPaciente crearPerfil(PerfilPacienteRequest req) {
        // 1. Buscamos al usuario en nuestra BD usando el idAuth
        Usuario usuario = usuarioRepo.findByIdAuth(req.getIdAuth())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con el idAuth proporcionado"));

        // 2. Verificamos que no tenga un perfil ya creado
        if (perfilRepo.findByIdAuth(req.getIdAuth()).isPresent()) {
            throw new IllegalStateException("El paciente ya tiene un perfil configurado.");
        }

        // 3. Creamos el perfil usando el ID real que encontramos en la BD
        PerfilPaciente perfil = new PerfilPaciente();
        perfil.setPacienteId(usuario.getId());
        perfil.setIdAuth(req.getIdAuth());
        perfil.setPrevision(req.getPrevision());
        perfil.setTelefonoContacto(req.getTelefonoContacto());

        return perfilRepo.save(perfil);
    }

    @Transactional
    public PerfilPaciente actualizarPerfil(Long id, PerfilPacienteRequest req) {
        PerfilPaciente perfil = perfilRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Perfil no encontrado"));

        perfil.setPrevision(req.getPrevision());
        perfil.setTelefonoContacto(req.getTelefonoContacto());

        return perfilRepo.save(perfil);
    }
}