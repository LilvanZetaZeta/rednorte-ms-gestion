package cl.rednorte.ms_gestion.repository;

import cl.rednorte.ms_gestion.entity.CentroMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CentroMedicoRepository extends JpaRepository<CentroMedico, Long> {

  
}
