package cl.rednorte.ms_registro.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import cl.rednorte.ms_registro.enums.RolEnum;
import lombok.Data;

@Data
public class UsuarioResponseDTO {
    private UUID id;
    private String rut;
    private String nombreCompleto;
    private String correo;
    private RolEnum rol;
    private LocalDateTime creadoEn;
}