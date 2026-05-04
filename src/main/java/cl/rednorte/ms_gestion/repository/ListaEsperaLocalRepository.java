package cl.rednorte.ms_gestion.repository;

import cl.rednorte.ms_gestion.entity.ListaEsperaLocal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ListaEsperaLocalRepository extends JpaRepository<ListaEsperaLocal, Long> {
    List<ListaEsperaLocal> findByCentroIdOrderByPrioridadAsc(Long centroId);
}
