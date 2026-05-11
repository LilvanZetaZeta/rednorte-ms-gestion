package cl.rednorte.ms_gestion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rednorte.ms_gestion.entity.CentroMedico;

@Repository
public interface CentroMedicoRepository extends JpaRepository<CentroMedico, Long> {

    List<CentroMedico> findByRegionAndComunaIgnoreCase(String region, String comuna);

    List<CentroMedico> findByComunaIgnoreCase(String comuna);
}