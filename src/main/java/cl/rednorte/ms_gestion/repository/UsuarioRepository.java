package cl.rednorte.ms_gestion.repository;

import cl.rednorte.ms_gestion.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
    Optional<Usuario> findByIdAuth(String idAuth); 
    boolean existsByCorreo(String correo);
    boolean existsByRut(String rut);
}