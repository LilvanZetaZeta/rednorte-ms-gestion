package cl.rednorte.ms_gestion.repository;

import cl.rednorte.ms_gestion.entity.CentroMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CentroMedicoRepository extends JpaRepository<CentroMedico, Long> {
    // Búsquedas personalizadas
    List<CentroMedico> findByRegionAndComunaIgnoreCase(String region, String comuna);
    List<CentroMedico> findByComunaIgnoreCase(String comuna);
}