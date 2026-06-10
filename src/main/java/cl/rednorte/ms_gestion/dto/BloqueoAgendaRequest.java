package cl.rednorte.ms_gestion.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BloqueoAgendaRequest {
    @NotNull(message = "El ID del médico es obligatorio")
    private Long medicoId;
    
    @NotNull(message = "La fecha de bloqueo es obligatoria")
    private LocalDate fechaBloqueo;
} 