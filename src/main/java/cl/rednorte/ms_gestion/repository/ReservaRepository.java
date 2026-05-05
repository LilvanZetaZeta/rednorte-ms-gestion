package cl.rednorte.ms_gestion.repository;

import cl.rednorte.ms_gestion.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByPaciente_IdAuth(String idAuth);
    List<Reserva> findByPacienteId(Long pacienteId);
    List<Reserva> findByCentroId(Long centroId);
    List<Reserva> findByMedicoId(Long medicoId); 
}