package cl.rednorte.ms_gestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransferenciaReservaRequest {
    @NotNull(message = "El ID de la reserva original es obligatorio")
    private Long reservaOriginalId;

    @NotBlank(message = "El ID del nuevo paciente es obligatorio")
    private String nuevoPacienteId;
}
