package cl.rednorte.ms_gestion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CentroMetricaDTO {
    private String nombreCentro;
    private long cantidadReservas;
}
