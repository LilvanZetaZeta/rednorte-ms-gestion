package cl.rednorte.ms_gestion.dto;

import cl.rednorte.ms_gestion.entity.Reserva.OrigenReserva;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservaRequest {
    @NotNull(message = "El ID del paciente es requerido")
    private Long pacienteId;
    @NotNull(message = "El ID del médico es requerido")
    private Long medicoId;
    @NotNull(message = "El ID del centro es requerido")
    private Long centroId;
    @NotNull(message = "La fecha y hora son requeridas")
    private LocalDateTime fechaHora;
    private OrigenReserva origen = OrigenReserva.WEB;
}