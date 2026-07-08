package cl.rednorte.ms_gestion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cl.rednorte.ms_gestion.dto.BloqueoAgendaRequest;
import cl.rednorte.ms_gestion.dto.ReservaRequest;
import cl.rednorte.ms_gestion.dto.TransferenciaReservaRequest;
import cl.rednorte.ms_gestion.entity.Reserva;
import cl.rednorte.ms_gestion.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservaController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva la seguridad perimetral de JWT para aislar el controlador
class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal") 
    @MockBean
    private ReservaService reservaService;

    @Autowired
    private ObjectMapper objectMapper;

    private ReservaRequest requestReserva;
    private BloqueoAgendaRequest requestBloqueo;
    private Reserva reservaMock;

    @BeforeEach
    void setUp() {
        // Inicializar DTO de Reserva
        requestReserva = new ReservaRequest();
        requestReserva.setCentroId(1L);
        requestReserva.setMedicoId(20L);
        requestReserva.setFechaHora(LocalDateTime.now().plusDays(2));

        // Inicializar DTO de Bloqueo
        requestBloqueo = new BloqueoAgendaRequest();
        requestBloqueo.setMedicoId(57L);
        requestBloqueo.setFechaBloqueo(LocalDate.of(2026, 6, 26));

        // Inicializar Entidad Reserva Mock de salida
        reservaMock = new Reserva();
        reservaMock.setId(100L);
        reservaMock.setFechaHora(requestReserva.getFechaHora());
        reservaMock.setEstado(Reserva.EstadoReserva.VIGENTE);
    }

    // ==========================================
    // TESTS PARA: POST / (crear)
    // ==========================================

    @Test
    @DisplayName("POST /api/gestion/reservas -> Debe retornar 200 OK y la reserva creada")
    void crear_Valido_RetornaReserva() throws Exception {
        Mockito.when(reservaService.crear(any(ReservaRequest.class))).thenReturn(reservaMock);

        mockMvc.perform(post("/api/gestion/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestReserva)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.estado").value("VIGENTE"));
    }

    @Test
    @DisplayName("POST /api/gestion/reservas -> Debe retornar 400 Bad Request si el servicio lanza RuntimeException")
    void crear_LógicaInvalida_RetornaBadRequestConError() throws Exception {
        Mockito.when(reservaService.crear(any(ReservaRequest.class)))
                .thenThrow(new RuntimeException("El médico no atiende en el horario seleccionado"));

        mockMvc.perform(post("/api/gestion/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestReserva)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El médico no atiende en el horario seleccionado"));
    }

    // ==========================================
    // TESTS PARA: POST /reasignaciones/agenda/bloquear
    // ==========================================

    @Test
    @DisplayName("POST /api/gestion/reservas/reasignaciones/agenda/bloquear -> Debe retornar 200 OK y mensaje de éxito")
    void bloquearAgenda_Valido_RetornaMensajeExito() throws Exception {
        Mockito.doNothing().when(reservaService).bloquearAgendaYProcesarCitas(any(BloqueoAgendaRequest.class));

        mockMvc.perform(post("/api/gestion/reservas/reasignaciones/agenda/bloquear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBloqueo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Agenda bloqueada exitosamente. Las citas del día han sido canceladas para reasignación."));
    }

    @Test
    @DisplayName("POST /api/gestion/reservas/reasignaciones/agenda/bloquear -> Debe retornar 400 Bad Request si falla el proceso")
    void bloquearAgenda_ErrorServicio_RetornaBadRequest() throws Exception {
        Mockito.doThrow(new RuntimeException("El médico ya se encuentra bloqueado para esa fecha"))
                .when(reservaService).bloquearAgendaYProcesarCitas(any(BloqueoAgendaRequest.class));

        mockMvc.perform(post("/api/gestion/reservas/reasignaciones/agenda/bloquear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBloqueo)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El médico ya se encuentra bloqueado para esa fecha"));
    }

    // ==========================================
    // TESTS PARA: PUT /{id} (actualizar)
    // ==========================================

    @Test
    @DisplayName("PUT /api/gestion/reservas/{id} -> Debe retornar 200 OK y la reserva actualizada")
    void actualizar_Valido_RetornaReservaActualizada() throws Exception {
        Mockito.when(reservaService.actualizarTotal(eq(100L), any(ReservaRequest.class))).thenReturn(reservaMock);

        mockMvc.perform(put("/api/gestion/reservas/{id}", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestReserva)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L));
    }

    // ==========================================
    // TESTS PARA: PATCH /{id} (parchear)
    // ==========================================

    @Test
    @DisplayName("PATCH /api/gestion/reservas/{id} -> Debe aplicar parches parciales y retornar 200 OK")
    void parchear_Valido_RetornaReservaModificada() throws Exception {
        Map<String, Object> updates = new HashMap<>();
        updates.put("estado", "CONFIRMADA");
        
        reservaMock.setEstado(Reserva.EstadoReserva.CONFIRMADA);
        Mockito.when(reservaService.parchear(eq(100L), eq(updates))).thenReturn(reservaMock);

        mockMvc.perform(patch("/api/gestion/reservas/{id}", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));
    }

    // ==========================================
    // TESTS PARA: PUT /{id}/cancelar
    // ==========================================

    @Test
    @DisplayName("PUT /api/gestion/reservas/{id}/cancelar -> Debe retornar 200 OK y la reserva cancelada")
    void cancelar_Valido_RetornaReservaCancelada() throws Exception {
        reservaMock.setEstado(Reserva.EstadoReserva.CANCELADA);
        Mockito.when(reservaService.cancelar(100L)).thenReturn(reservaMock);

        mockMvc.perform(put("/api/gestion/reservas/{id}/cancelar", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));
    }

    @Test
    @DisplayName("PUT /api/gestion/reservas/{id}/cancelar -> Debe retornar 400 Bad Request si infringe políticas de tiempo")
    void cancelar_Menos24HorasAnticipacion_RetornaBadRequest() throws Exception {
        Mockito.when(reservaService.cancelar(100L))
                .thenThrow(new RuntimeException("No puede cancelar su cita con menos de 24 horas de anticipación."));

        mockMvc.perform(put("/api/gestion/reservas/{id}/cancelar", 100L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("No puede cancelar su cita con menos de 24 horas de anticipación."));
    }

    // ==========================================
    // TESTS PARA: DELETE /{id} (eliminar)
    // ==========================================

    @Test
    @DisplayName("DELETE /api/gestion/reservas/{id} -> Debe eliminar y responder con 204 No Content")
    void eliminar_IdExistente_RetornaNoContent() throws Exception {
        Mockito.doNothing().when(reservaService).eliminar(100L);

        mockMvc.perform(delete("/api/gestion/reservas/{id}", 100L))
                .andExpect(status().isNoContent());

        Mockito.verify(reservaService, Mockito.times(1)).eliminar(100L);
    }

    // ==========================================
    // TESTS PARA: POST /transferir
    // ==========================================

    @Test
    @DisplayName("POST /api/gestion/reservas/transferir -> Debe retornar 200 OK y la reserva transferida")
    void transferir_Valido_RetornaReservaTransferida() throws Exception {
        TransferenciaReservaRequest requestTransferencia = new TransferenciaReservaRequest();
        requestTransferencia.setReservaOriginalId(100L);
        requestTransferencia.setNuevoPacienteId("uuid-paciente-candidato");

        reservaMock.setEstado(Reserva.EstadoReserva.VIGENTE);
        Mockito.when(reservaService.transferir(any(TransferenciaReservaRequest.class))).thenReturn(reservaMock);

        mockMvc.perform(post("/api/gestion/reservas/transferir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestTransferencia)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.estado").value("VIGENTE"));
    }

    @Test
    @DisplayName("POST /api/gestion/reservas/transferir -> Debe retornar 400 Bad Request si el servicio lanza RuntimeException")
    void transferir_ReservaNoCancelada_RetornaBadRequest() throws Exception {
        TransferenciaReservaRequest requestTransferencia = new TransferenciaReservaRequest();
        requestTransferencia.setReservaOriginalId(100L);
        requestTransferencia.setNuevoPacienteId("uuid-paciente-candidato");

        Mockito.when(reservaService.transferir(any(TransferenciaReservaRequest.class)))
                .thenThrow(new RuntimeException("Solo se puede transferir una reserva cancelada. Estado actual: VIGENTE"));

        mockMvc.perform(post("/api/gestion/reservas/transferir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestTransferencia)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Solo se puede transferir una reserva cancelada. Estado actual: VIGENTE"));
    }
}