package cl.rednorte.ms_gestion.repository;

import cl.rednorte.ms_gestion.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByPacienteId(Long pacienteId);
    List<Reserva> findByCentroId(Long centroId);
}
