package cl.rednorte.ms_registro.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rednorte.ms_registro.entity.CentroMedico;

@Repository
public interface CentroMedicoRepository extends JpaRepository<CentroMedico, UUID> {
    boolean existsByNombreSucursal(String nombreSucursal);
}