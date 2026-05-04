package cl.rednorte.ms_gestion.repository;

import cl.rednorte.ms_gestion.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    // El guion bajo (_) es crucial para evitar ambigüedades en la navegación de propiedades
    List<Reserva> findByPaciente_IdAuth(String idAuth);
    
    List<Reserva> findByPacienteId(Long pacienteId);
    List<Reserva> findByCentroId(Long centroId);
}