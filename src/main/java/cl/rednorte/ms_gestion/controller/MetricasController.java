package cl.rednorte.ms_gestion.controller;

import cl.rednorte.ms_gestion.dto.DashboardResumenDTO;
import cl.rednorte.ms_gestion.dto.CentroMetricaDTO;
import cl.rednorte.ms_gestion.service.MetricasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/gestion/metricas")
public class MetricasController {
    @Autowired private MetricasService metricasService;

    @GetMapping("/resumen")
    public DashboardResumenDTO getResumen() { 
        return metricasService.obtenerResumen(); 
    }

    @GetMapping("/centros")
    public List<CentroMetricaDTO> getPorCentro() { 
        return metricasService.obtenerMetricasPorCentro(); 
    }
}