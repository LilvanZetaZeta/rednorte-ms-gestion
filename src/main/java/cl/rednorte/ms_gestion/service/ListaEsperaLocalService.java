package cl.rednorte.ms_gestion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import cl.rednorte.ms_gestion.entity.ListaEsperaLocal;
import cl.rednorte.ms_gestion.repository.ListaEsperaLocalRepository;

@Service
public class ListaEsperaLocalService {

    @Autowired
    private ListaEsperaLocalRepository listaEsperaLocalRepository;

    public List<ListaEsperaLocal> obtenerPorPaciente(Long pacienteId) {
        return listaEsperaLocalRepository.findByPacienteId(pacienteId);
    }
}