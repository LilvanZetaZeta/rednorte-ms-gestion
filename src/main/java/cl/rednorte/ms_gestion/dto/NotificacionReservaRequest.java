package cl.rednorte.ms_gestion.dto;

import lombok.Data;

@Data
public class NotificacionReservaRequest {
    private Long pacienteId;
    private String correoDestino;
    private String nombrePaciente;
    private String nombreMedico;
    private String nombreCentro;
    private String fechaHoraReserva;
}