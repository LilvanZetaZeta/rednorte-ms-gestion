package cl.rednorte.ms_gestion.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

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
    private String rut;

    @Column(name = "nombre_completo", nullable = false, length = 200)
    private String nombreCompleto;

    @Column(name = "correo", unique = true, nullable = false, length = 150)
    private String correo;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, columnDefinition = "rol_usuario")
    private RolUsuario rol = RolUsuario.PACIENTE;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_especialidad",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "especialidad_id")
    )
    private List<Especialidad> especialidades;

    public enum RolUsuario { PACIENTE, MEDICO, ADMINISTRATIVO, DIRECTOR }
}