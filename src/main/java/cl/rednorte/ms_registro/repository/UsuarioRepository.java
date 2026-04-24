package cl.rednorte.ms_registro.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rednorte.ms_registro.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    // Spring Data JPA crea la consulta SQL automáticamente solo con leer el nombre del método
    boolean existsByRut(String rut);
    boolean existsByCorreo(String correo);
}