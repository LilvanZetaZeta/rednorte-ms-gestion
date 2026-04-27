package cl.rednorte.ms_registro.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioRequestDTO {

    @NotBlank(message = "El RUT no puede estar vacío")
    @Size(max = 12, message = "El RUT no puede exceder los 12 caracteres")
    private String rut;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder los 150 caracteres")
    private String nombreCompleto;

    @NotBlank(message = "El correo no puede estar vacío")
    @Email(message = "Debe ser un formato de correo electrónico válido")
    @Size(max = 150, message = "El correo no puede exceder los 150 caracteres")
    private String correo;
}