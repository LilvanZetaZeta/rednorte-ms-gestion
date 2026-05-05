package cl.rednorte.ms_gestion.dto;

import cl.rednorte.ms_gestion.entity.Usuario.RolUsuario;
import lombok.Data;

@Data
public class RegistroRequest {
    private String idAuth; 
    private String rut;
    private String nombreCompleto;
    private String correo;
    private RolUsuario rol;
}