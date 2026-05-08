package cl.rednorte.ms_gestion.repository;

import cl.rednorte.ms_gestion.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);

    List<Usuario> findByRolNot(Usuario.RolUsuario rol);

    Optional<Usuario> findByIdAuth(String idAuth);

    boolean existsByCorreo(String correo);

    boolean existsByRut(String rut);

    List<Usuario> findByRolAndEspecialidades_NombreIgnoreCase(Usuario.RolUsuario rol, String especialidad);

    @Query(value = "SELECT count(*) FROM usuario WHERE CAST(rol AS text) = :#{#rol.name()}", nativeQuery = true)
    long countByRol(@Param("rol") Usuario.RolUsuario rol);
}