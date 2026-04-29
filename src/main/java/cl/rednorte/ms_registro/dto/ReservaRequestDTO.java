package cl.rednorte.ms_registro.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservaRequestDTO {

    @NotNull(message = "El ID del paciente es obligatorio")
    private UUID pacienteId;

    @NotNull(message = "El ID del centro médico es obligatorio")
    private UUID centroId;

    @NotNull(message = "La fecha y hora de la reserva es obligatoria")
    @FutureOrPresent(message = "La reserva debe ser para una fecha actual o futura")
    private LocalDateTime fechaHora;

    @NotBlank(message = "El origen de la reserva es obligatorio (ej: WEB, PRESENCIAL)")
    private String origen;
}