package cl.rednorte.ms_gestion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "El correo es requerido")
    private String correo;
    @NotBlank(message = "La contraseña es requerida")
    private String contrasena;
}