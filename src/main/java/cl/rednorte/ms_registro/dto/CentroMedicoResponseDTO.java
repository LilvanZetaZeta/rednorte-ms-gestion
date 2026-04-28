package cl.rednorte.ms_registro.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class CentroMedicoResponseDTO {
    private UUID id;
    private String nombreSucursal;
}