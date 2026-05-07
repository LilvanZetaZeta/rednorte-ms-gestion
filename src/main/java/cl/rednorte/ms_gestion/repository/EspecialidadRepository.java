package cl.rednorte.ms_gestion.repository;

import cl.rednorte.ms_gestion.entity.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {
    Optional<Especialidad> findByNombreIgnoreCase(String nombre);
}