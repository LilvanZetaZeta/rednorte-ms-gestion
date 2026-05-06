package cl.rednorte.ms_gestion.controller;

import cl.rednorte.ms_gestion.dto.CentroMetricaDTO;
import cl.rednorte.ms_gestion.dto.DashboardResumenDTO;
import cl.rednorte.ms_gestion.entity.Reserva;
import cl.rednorte.ms_gestion.entity.Usuario;
import cl.rednorte.ms_gestion.repository.CentroMedicoRepository;
import cl.rednorte.ms_gestion.repository.ReservaRepository;
import cl.rednorte.ms_gestion.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/gestion/metricas")
public class MetricasController {

    @Autowired private ReservaRepository reservaRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CentroMedicoRepository centroMedicoRepository;

    @GetMapping("/resumen")
    public DashboardResumenDTO getResumen() {
        DashboardResumenDTO dto = new DashboardResumenDTO();
        dto.setTotalReservas(reservaRepository.count());
        dto.setReservasVigentes(reservaRepository.countByEstado(Reserva.EstadoReserva.VIGENTE));
        dto.setReservasCanceladas(reservaRepository.countByEstado(Reserva.EstadoReserva.CANCELADA));
        dto.setTotalPacientes(usuarioRepository.countByRol(Usuario.RolUsuario.PACIENTE));
        dto.setTotalMedicos(usuarioRepository.countByRol(Usuario.RolUsuario.MEDICO));
        dto.setTotalCentros(centroMedicoRepository.count());
        return dto;
    }

    @GetMapping("/centros")
    public List<CentroMetricaDTO> getMetricasPorCentro() {
        return centroMedicoRepository.findAll().stream().map(centro -> {
            long count = reservaRepository.findByCentroId(centro.getId()).size();
            return new CentroMetricaDTO(centro.getNombreSucursal(), count);
        }).collect(Collectors.toList());
    }
}
