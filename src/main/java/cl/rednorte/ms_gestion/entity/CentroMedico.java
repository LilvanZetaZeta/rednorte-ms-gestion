package cl.rednorte.ms_gestion.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "centro_medico")
public class CentroMedico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_sucursal", nullable = false, length = 200)
    private String nombreSucursal;
}
