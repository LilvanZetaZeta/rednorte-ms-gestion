package cl.rednorte.ms_registro.enums;

public enum EstadoListaEsperaEnum {
    EN_ESPERA,   // El paciente está haciendo fila
    NOTIFICADO,  // Se liberó un cupo y se le avisó al paciente
    ASIGNADO,    // El paciente aceptó el cupo y se transformó en una Reserva real
    CANCELADO    // El paciente se aburrió de esperar o ya no necesita la cita
}