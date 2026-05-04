package cl.rednorte.ms_gestion.dto;

import lombok.Data;

@Data
public class ListaEsperaRequest {
    private Long centroId;
    private Long pacienteId;
    private Integer prioridad;
}
