package cl.rednorte.ms_gestion.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_cita")
@Data
public class HistorialCita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Usuario paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private Usuario medico;

    @Column(nullable = false)
    private LocalDateTime fechaAtencion;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String observaciones;

    @Column(columnDefinition = "TEXT")
    private String procedimientoRealizado;
}