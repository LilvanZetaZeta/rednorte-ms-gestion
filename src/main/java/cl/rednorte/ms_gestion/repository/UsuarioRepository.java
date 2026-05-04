package cl.rednorte.ms_gestion.repository;

import cl.rednorte.ms_gestion.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
    
    // Búsqueda necesaria para vincular la sesión de Supabase con tu DB
    Optional<Usuario> findByIdAuth(String idAuth); 
    
    boolean existsByCorreo(String correo);
    boolean existsByRut(String rut);
}