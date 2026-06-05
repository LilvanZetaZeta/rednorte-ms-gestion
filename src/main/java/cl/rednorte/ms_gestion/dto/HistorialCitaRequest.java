package cl.rednorte.ms_gestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HistorialCitaRequest {
    @NotNull(message = "El ID del paciente es obligatorio")
    private Long pacienteId;

    @NotNull(message = "El ID del médico es obligatorio")
    private Long medicoId;

    @NotBlank(message = "Las observaciones clínicas son obligatorias")
    private String observaciones;

    private String procedimientoRealizado;

    private Long reservaId;
}