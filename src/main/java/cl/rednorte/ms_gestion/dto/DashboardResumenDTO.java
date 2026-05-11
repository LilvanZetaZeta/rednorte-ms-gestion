package cl.rednorte.ms_gestion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResumenDTO {
    private long totalReservas;
    private long reservasVigentes;
    private long reservasCanceladas;
    private long totalPacientes;
    private long totalMedicos;
    private long totalCentros;
    private long totalEspecialidades;
}