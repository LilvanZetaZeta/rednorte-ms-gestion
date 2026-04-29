package cl.rednorte.ms_registro.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import cl.rednorte.ms_registro.enums.EstadoListaEsperaEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lista_espera")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListaEspera {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // El paciente que está haciendo la fila
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Usuario paciente;

    // La sucursal en la que está esperando cupo
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "centro_id", nullable = false)
    private CentroMedico centroMedico;

    // Este campo es intocable: define quién va primero
    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    // Estado por defecto
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoListaEsperaEnum estado = EstadoListaEsperaEnum.EN_ESPERA;
}