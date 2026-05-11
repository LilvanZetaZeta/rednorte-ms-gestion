package cl.rednorte.ms_gestion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rednorte.ms_gestion.entity.ListaEsperaLocal;

@Repository
public interface ListaEsperaLocalRepository extends JpaRepository<ListaEsperaLocal, Long> {
    List<ListaEsperaLocal> findByCentroIdOrderByPrioridadAsc(Long centroId);
    List<ListaEsperaLocal> findByPacienteId(Long pacienteId);
}