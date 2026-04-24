package cl.rednorte.ms_registro.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rednorte.ms_registro.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    boolean existsByRut(String rut);
    boolean existsByCorreo(String correo);
    
    // Método para buscar por RUT
    Optional<Usuario> findByRut(String rut);
}