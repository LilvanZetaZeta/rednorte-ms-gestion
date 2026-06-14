package cl.rednorte.ms_gestion.entity;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import cl.rednorte.ms_gestion.validation.ValidRut;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_auth", unique = true, nullable = false, length = 50)
    private String idAuth;

    @Column(name = "rut", unique = true, nullable = false, length = 12)
    @NotBlank(message = "El RUT es requerido")
    @ValidRut(message = "El RUT no es válido (dígito verificador incorrecto)")
    private String rut;

    @Column(name = "nombre_completo", nullable = false, length = 200)
    @NotBlank(message = "El nombre es requerido")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[a-záéíóúñüA-ZÁÉÍÓÚÑÜ\\s]+$", message = "El nombre solo puede contener letras y espacios")
    private String nombreCompleto;

    @Column(name = "correo", unique = true, nullable = false, length = 150)
    @NotBlank(message = "El correo es requerido")
    @Email(message = "Por favor ingresa un correo válido (ej: usuario@ejemplo.com)")
    @Size(max = 255, message = "El correo no puede exceder 255 caracteres")
    private String correo;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "rol", nullable = false, columnDefinition = "rol_usuario")
    private RolUsuario rol = RolUsuario.PACIENTE;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_especialidad",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "especialidad_id")
    )
    private List<Especialidad> especialidades;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "centro_medico_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private CentroMedico centroMedico;

    public enum RolUsuario { PACIENTE, MEDICO, ADMINISTRATIVO, DIRECTOR, SECRETARIA }
}