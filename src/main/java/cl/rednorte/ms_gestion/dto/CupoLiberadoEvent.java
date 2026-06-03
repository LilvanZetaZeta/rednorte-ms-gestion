package cl.rednorte.ms_gestion.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CupoLiberadoEvent {
    private Long medicoId;
    private Long centroId;
    private LocalDateTime fechaHora;
    private String especialidad;
    private String tipoProcedimiento;
}
