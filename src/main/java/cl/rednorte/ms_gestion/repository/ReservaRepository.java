package cl.rednorte.ms_gestion.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cl.rednorte.ms_gestion.entity.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    //PORTAL SE ENCARGA DE LAS LISTAS
    List<Reserva> findByPaciente_IdAuth(String idAuth);
    List<Reserva> findByPacienteId(Long pacienteId);
    List<Reserva> findByCentroId(Long centroId);
    List<Reserva> findByMedicoId(Long medicoId); 
    
    @Query(value = "SELECT count(*) FROM reserva WHERE CAST(estado AS text) = :#{#estado.name()}", nativeQuery = true)
    long countByEstado(@Param("estado") Reserva.EstadoReserva estado);

    @Query("SELECT r FROM Reserva r " +
           "WHERE r.medico.id = :medicoId " +
           "AND r.fechaHora BETWEEN :inicio AND :fin " +
           "AND r.estado IN :estados")
    List<Reserva> findByMedicoIdAndFechaHoraBetweenAndEstadoIn(
            @Param("medicoId") Long medicoId, 
            @Param("inicio") LocalDateTime inicio, 
            @Param("fin") LocalDateTime fin, 
            @Param("estados") List<Reserva.EstadoReserva> estados);
}