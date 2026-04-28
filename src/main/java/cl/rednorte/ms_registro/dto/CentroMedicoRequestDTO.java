package cl.rednorte.ms_registro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CentroMedicoRequestDTO {

    @NotBlank(message = "El nombre de la sucursal no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String nombreSucursal;
}