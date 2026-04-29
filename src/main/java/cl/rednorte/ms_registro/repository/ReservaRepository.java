package cl.rednorte.ms_registro.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rednorte.ms_registro.entity.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, UUID> {
    
    // Busca todas las reservas que le pertenecen a un paciente específico
    List<Reserva> findByPacienteId(UUID pacienteId);

    // Busca todas las reservas agendadas en una sucursal específica
    List<Reserva> findByCentroMedicoId(UUID centroId);
    
}
