package cl.rednorte.ms_gestion.dto;

import cl.rednorte.ms_gestion.entity.Usuario.RolUsuario;
import cl.rednorte.ms_gestion.validation.ValidRut;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class RegistroRequest {
    private String idAuth;

    @NotBlank(message = "El RUT es requerido")
    @ValidRut(message = "El RUT no es válido (dígito verificador incorrecto)")
    private String rut;

    @NotBlank(message = "El nombre es requerido")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[a-záéíóúñüA-ZÁÉÍÓÚÑÜ\\s]+$", message = "El nombre solo puede contener letras y espacios")
    private String nombreCompleto;

    @NotBlank(message = "El correo es requerido")
    @Email(message = "Por favor ingresa un correo válido (ej: usuario@ejemplo.com)")
    @Size(max = 255, message = "El correo no puede exceder 255 caracteres")
    private String correo;

    private RolUsuario rol;
    private List<Long> especialidadIds; // Para médicos
}