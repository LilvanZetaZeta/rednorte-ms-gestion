package cl.rednorte.ms_gestion.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "perfil_paciente")
@Data
public class PerfilPaciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "paciente_id", nullable = false, unique = true)
    private Long pacienteId;

    @Column(name = "id_auth", nullable = false, unique = true)
    private String idAuth;

    private String prevision;
    private String telefonoContacto;
}