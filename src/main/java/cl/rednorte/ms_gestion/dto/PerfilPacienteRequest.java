package cl.rednorte.ms_gestion.dto;

import lombok.Data;

@Data
public class PerfilPacienteRequest {
    private Long pacienteId;
    private String idAuth;
    private String prevision;
    private String telefonoContacto;
}