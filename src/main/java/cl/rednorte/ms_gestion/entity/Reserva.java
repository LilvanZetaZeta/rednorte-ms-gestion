package cl.rednorte.ms_gestion.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CAMBIO 1: Usamos EAGER para asegurar que el paciente viaje al frontend
    // CAMBIO 2: JsonIgnoreProperties evita errores con los proxies de Hibernate
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paciente_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Usuario paciente;

    // CAMBIO 3: También para el centro médico para mostrar el nombre en el portal
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "centro_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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