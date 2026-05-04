package cl.rednorte.ms_gestion.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Usuario paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centro_id", nullable = false)
    private CentroMedico centro;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "origen", nullable = false, columnDefinition = "origen_reserva")
    private OrigenReserva origen = OrigenReserva.WEB;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, columnDefinition = "estado_reserva")
    private EstadoReserva estado = EstadoReserva.VIGENTE;

    public enum OrigenReserva { WEB, PRESENCIAL }
    public enum EstadoReserva { VIGENTE, CANCELADA }
}
