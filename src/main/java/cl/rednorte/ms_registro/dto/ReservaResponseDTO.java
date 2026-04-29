package cl.rednorte.ms_registro.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import cl.rednorte.ms_registro.enums.EstadoReservaEnum;
import lombok.Data;

@Data
public class ReservaResponseDTO {
    private UUID id;
    private String pacienteNombre;
    private String centroNombre;
    private LocalDateTime fechaHora;
    private EstadoReservaEnum estado;
}