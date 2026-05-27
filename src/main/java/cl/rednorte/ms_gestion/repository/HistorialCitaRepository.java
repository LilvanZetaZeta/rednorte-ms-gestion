package cl.rednorte.ms_gestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cl.rednorte.ms_gestion.entity.HistorialCita;

@Repository
public interface HistorialCitaRepository extends JpaRepository<HistorialCita, Long> {
}