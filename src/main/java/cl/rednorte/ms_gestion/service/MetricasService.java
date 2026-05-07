package cl.rednorte.ms_gestion.service;

import cl.rednorte.ms_gestion.dto.*;
import cl.rednorte.ms_gestion.repository.*;
import cl.rednorte.ms_gestion.entity.Reserva;
import cl.rednorte.ms_gestion.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MetricasService {
    @Autowired private ReservaRepository reservaRepo;
    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private CentroMedicoRepository centroRepo;
    @Autowired private EspecialidadRepository espRepo;

    public DashboardResumenDTO obtenerResumen() {
        return new DashboardResumenDTO(
            reservaRepo.count(),
            reservaRepo.countByEstado(Reserva.EstadoReserva.VIGENTE),
            reservaRepo.countByEstado(Reserva.EstadoReserva.CANCELADA),
            usuarioRepo.countByRol(Usuario.RolUsuario.PACIENTE),
            usuarioRepo.countByRol(Usuario.RolUsuario.MEDICO),
            centroRepo.count(),
            espRepo.count()
        );
    }

    public List<CentroMetricaDTO> obtenerMetricasPorCentro() {
        return centroRepo.findAll().stream()
            .map(c -> new CentroMetricaDTO(c.getNombreSucursal(), reservaRepo.findByCentroId(c.getId()).size()))
            .collect(Collectors.toList());
    }
}