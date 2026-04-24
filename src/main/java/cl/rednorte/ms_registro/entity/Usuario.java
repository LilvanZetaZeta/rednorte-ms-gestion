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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "El RUT no puede estar vacío")
    @Size(max = 12, message = "El RUT no puede exceder los 12 caracteres")
    @Column(unique = true, nullable = false, length = 12)
    private String rut;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder los 150 caracteres")
    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @NotBlank(message = "El correo no puede estar vacío")
    @Email(message = "Debe ser un formato de correo electrónico válido")
    @Size(max = 150, message = "El correo no puede exceder los 150 caracteres")
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