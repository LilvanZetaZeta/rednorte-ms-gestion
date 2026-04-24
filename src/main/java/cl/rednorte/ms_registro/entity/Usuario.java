package cl.rednorte.ms_registro.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import cl.rednorte.ms_registro.enums.RolEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, length = 12)
    private String rut;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @Column(unique = true, nullable = false, length = 150)
    private String correo;

    @Column(name = "auth0_id", unique = true, length = 100)
    private String auth0Id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolEnum rol = RolEnum.PACIENTE;

    @Column(name = "creado_en", insertable = false, updatable = false)
    private LocalDateTime creadoEn;
}