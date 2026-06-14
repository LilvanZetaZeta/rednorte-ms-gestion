package cl.rednorte.ms_gestion.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PerfilPacienteRequest {
    private Long pacienteId;
    private String idAuth;

    @Size(max = 255, message = "La previsión no puede exceder 255 caracteres")
    private String prevision;

    @Size(max = 255, message = "El teléfono no puede exceder 255 caracteres")
    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Por favor ingresa un teléfono válido (9-15 números)")
    private String telefonoContacto;

    public void setTelefonoContacto(String telefonoContacto) {
        if (telefonoContacto != null) {
            this.telefonoContacto = telefonoContacto.replaceAll("[\\s\\-]", "");
        } else {
            this.telefonoContacto = null;
        }
    }
}