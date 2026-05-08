package cl.rednorte.ms_gestion.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "centro_medico")
public class CentroMedico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_sucursal", nullable = false, length = 200)
    @NotBlank(message = "El nombre de la sucursal es requerido")
    private String nombreSucursal;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "La región es requerida")
    private String region;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "La comuna es requerida")
    private String comuna;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "La dirección es requerida")
    private String direccion;
}