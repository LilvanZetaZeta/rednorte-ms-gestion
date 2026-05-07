package cl.rednorte.ms_gestion.dto;

import cl.rednorte.ms_gestion.entity.Usuario.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Long id;
    private String correo;
    private String nombreCompleto;
    private RolUsuario rol;
}