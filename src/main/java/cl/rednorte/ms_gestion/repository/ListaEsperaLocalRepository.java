package cl.rednorte.ms_gestion.repository;

import cl.rednorte.ms_gestion.entity.ListaEsperaLocal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListaEsperaLocalRepository extends JpaRepository<ListaEsperaLocal, Long> {
    List<ListaEsperaLocal> findByCentroIdOrderByPrioridadAsc(Long centroId);
    List<ListaEsperaLocal> findByPacienteId(Long pacienteId);
}
