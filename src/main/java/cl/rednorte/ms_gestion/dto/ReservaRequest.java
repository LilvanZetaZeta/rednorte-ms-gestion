package cl.rednorte.ms_gestion.dto;

import java.time.LocalDateTime;

import cl.rednorte.ms_gestion.entity.Reserva.OrigenReserva;
import cl.rednorte.ms_gestion.entity.Reserva.TipoReserva;
import cl.rednorte.ms_gestion.validation.ValidRut;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReservaRequest {
    
    // Ahora es opcional porque puede venir solo el RUT si es paciente nuevo
    private Long pacienteId;
    
    @ValidRut
    private String pacienteRut;

    @Email(message = "Por favor ingresa un correo válido (ej: usuario@ejemplo.com)")
    @Size(max = 255, message = "El correo no puede exceder 255 caracteres")
    private String pacienteCorreo;

    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[a-záéíóúñüA-ZÁÉÍÓÚÑÜ\\s]+$", message = "El nombre solo puede contener letras y espacios")
    private String pacienteNombreCompleto;

    @NotNull(message = "El ID del médico es requerido")
    private Long medicoId;
    
    @NotNull(message = "El ID del centro es requerido")
    private Long centroId;
    
    @NotNull(message = "La fecha y hora son requeridas")
    private LocalDateTime fechaHora;

    @NotNull(message = "El tipo de reserva es requerido")
    private TipoReserva tipoReserva = TipoReserva.CONSULTA_MEDICA;
    
    private OrigenReserva origen = OrigenReserva.WEB;
}