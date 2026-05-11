package cl.rednorte.ms_gestion.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ListaEsperaRequest {
    @NotNull(message = "El ID del centro es requerido")
    private Long centroId;
    @NotNull(message = "El ID del paciente es requerido")
    private Long pacienteId;
    private Integer prioridad;
}