package cl.rednorte.ms_registro.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ListaEsperaRequestDTO {

    @NotNull(message = "El ID del paciente es obligatorio")
    private UUID pacienteId;

    @NotNull(message = "El ID del centro médico es obligatorio")
    private UUID centroId;
}