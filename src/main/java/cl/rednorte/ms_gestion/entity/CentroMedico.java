package cl.rednorte.ms_gestion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "centro_medico")
public class CentroMedico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_sucursal", unique = true, nullable = false, length = 200)
    @NotBlank(message = "El nombre de la sucursal es requerido")
    @Size(min = 3, message = "El nombre de la sucursal debe tener al menos 3 caracteres")
    @Pattern(regexp = ".*[a-zA-ZáéíóúñüÁÉÍÓÚÑÜ].*", message = "El nombre de la sucursal debe contener al menos una letra")
    private String nombreSucursal;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "La región es requerida")
    @Size(min = 3, message = "La región debe tener al menos 3 caracteres")
    @Pattern(regexp = ".*[a-zA-ZáéíóúñüÁÉÍÓÚÑÜ].*", message = "La región debe contener al menos una letra")
    private String region;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "La comuna es requerida")
    @Size(min = 3, message = "La comuna debe tener al menos 3 caracteres")
    @Pattern(regexp = ".*[a-zA-ZáéíóúñüÁÉÍÓÚÑÜ].*", message = "La comuna debe contener al menos una letra")
    private String comuna;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "La dirección es requerida")
    @Size(min = 5, message = "La dirección debe tener al menos 5 caracteres")
    @Pattern(regexp = ".*[a-zA-ZáéíóúñüÁÉÍÓÚÑÜ].*", message = "La dirección debe contener al menos una letra")
    private String direccion;
}