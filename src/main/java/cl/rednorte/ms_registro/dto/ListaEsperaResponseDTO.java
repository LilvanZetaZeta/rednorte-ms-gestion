package cl.rednorte.ms_registro.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import cl.rednorte.ms_registro.enums.EstadoListaEsperaEnum;
import lombok.Data;

@Data
public class ListaEsperaResponseDTO {
    private UUID id;
    private String pacienteNombre;
    private String centroNombre;
    private LocalDateTime fechaIngreso;
    private EstadoListaEsperaEnum estado;
}