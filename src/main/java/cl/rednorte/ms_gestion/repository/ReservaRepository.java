package cl.rednorte.ms_gestion.repository;

import cl.rednorte.ms_gestion.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByPaciente_IdAuth(String idAuth);
    List<Reserva> findByPacienteId(Long pacienteId);
    List<Reserva> findByCentroId(Long centroId);
    List<Reserva> findByMedicoId(Long medicoId); 
    
    @Query(value = "SELECT count(*) FROM reserva WHERE CAST(estado AS text) = :#{#estado.name()}", nativeQuery = true)
    long countByEstado(@Param("estado") Reserva.EstadoReserva estado);
}