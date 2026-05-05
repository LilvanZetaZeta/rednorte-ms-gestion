package cl.rednorte.ms_gestion.dto;

import cl.rednorte.ms_gestion.entity.Reserva.OrigenReserva;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservaRequest {
    private Long pacienteId;
    private Long medicoId; // ¡NUEVO!
    private Long centroId;
    private LocalDateTime fechaHora;
    private OrigenReserva origen = OrigenReserva.WEB;
}