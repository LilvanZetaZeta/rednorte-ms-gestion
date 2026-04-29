package cl.rednorte.ms_registro.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rednorte.ms_registro.entity.ListaEspera;
import cl.rednorte.ms_registro.enums.EstadoListaEsperaEnum;

@Repository
public interface ListaEsperaRepository extends JpaRepository<ListaEspera, UUID> {
    
    // Busca los pacientes en espera de una sucursal, ordenados por orden de llegada (FIFO)
    List<ListaEspera> findByCentroMedicoIdAndEstadoOrderByFechaIngresoAsc(UUID centroId, EstadoListaEsperaEnum estado);
    // Verifica si el paciente ya está haciendo fila en esa sucursal
    boolean existsByPacienteIdAndCentroMedicoIdAndEstado(UUID pacienteId, UUID centroId, EstadoListaEsperaEnum estado);
}