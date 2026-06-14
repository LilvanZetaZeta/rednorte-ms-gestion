package cl.rednorte.ms_gestion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rednorte.ms_gestion.entity.Especialidad;

@Repository
public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {
    //PORTAL SE ENCARGA DE LAS LISTAS
    Optional<Especialidad> findByNombreIgnoreCase(String nombre);
}