package cl.rednorte.ms_gestion.dto;

import java.time.LocalDateTime;

import cl.rednorte.ms_gestion.entity.Reserva.OrigenReserva;
import cl.rednorte.ms_gestion.validation.ValidRut;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservaRequest {
    
    // Ahora es opcional porque puede venir solo el RUT si es paciente nuevo
    private Long pacienteId;
    
    @ValidRut
    private String pacienteRut;
    private String pacienteCorreo;
    private String pacienteNombreCompleto;

    @NotNull(message = "El ID del médico es requerido")
    private Long medicoId;
    
    @NotNull(message = "El ID del centro es requerido")
    private Long centroId;
    
    @NotNull(message = "La fecha y hora son requeridas")
    private LocalDateTime fechaHora;
    
    private OrigenReserva origen = OrigenReserva.WEB;
}