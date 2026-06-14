package cl.rednorte.ms_gestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cl.rednorte.ms_gestion.entity.PerfilPaciente;
import java.util.Optional;

@Repository
public interface PerfilPacienteRepository extends JpaRepository<PerfilPaciente, Long> {
    Optional<PerfilPaciente> findByIdAuth(String idAuth);
}