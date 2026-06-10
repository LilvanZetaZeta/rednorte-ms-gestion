package cl.rednorte.ms_gestion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cl.rednorte.ms_gestion.dto.HistorialCitaRequest;
import cl.rednorte.ms_gestion.entity.HistorialCita;
import cl.rednorte.ms_gestion.entity.Usuario;
import cl.rednorte.ms_gestion.service.HistorialCitaService;
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

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HistorialCitaController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva filtros de seguridad para aislar el controlador
class HistorialCitaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private HistorialCitaService historialCitaService;

    @Autowired
    private ObjectMapper objectMapper;

    private HistorialCitaRequest requestValido;
    private HistorialCita historialMock;

    @BeforeEach
    void setUp() {
        // Mock de Request
        requestValido = new HistorialCitaRequest();
        requestValido.setPacienteId(10L);
        requestValido.setMedicoId(20L);
        requestValido.setReservaId(100L);
        requestValido.setObservaciones("Evolución excelente");
        requestValido.setProcedimientoRealizado("Control");

        // Entidades internas mockeadas para el retorno
        Usuario paciente = new Usuario();
        paciente.setId(10L);
        
        Usuario medico = new Usuario();
        medico.setId(20L);

        // Mock de Entidad respuesta
        historialMock = new HistorialCita();
        historialMock.setId(1L);
        historialMock.setPaciente(paciente);
        historialMock.setMedico(medico);
        historialMock.setReservaId(100L);
        historialMock.setObservaciones("Evolución excelente");
        historialMock.setProcedimientoRealizado("Control");
        historialMock.setFechaAtencion(LocalDateTime.now());
    }

    @Test
    @DisplayName("POST /api/gestion/historial-citas -> Debe retornar 200 OK y el historial creado si el payload es válido")
    void crear_PayloadValido_RetornaHistorial() throws Exception {
        Mockito.when(historialCitaService.crearRegistro(any(HistorialCitaRequest.class))).thenReturn(historialMock);

        mockMvc.perform(post("/api/gestion/historial-citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.reservaId").value(100L))
                .andExpect(jsonPath("$.procedimientoRealizado").value("Control"))
                .andExpect(jsonPath("$.paciente.id").value(10L));
    }

    @Test
    @DisplayName("POST /api/gestion/historial-citas -> Debe retornar 400 Bad Request si el DTO viola las validaciones de @Valid")
    void crear_PayloadInvalido_RetornaBadRequest() throws Exception {
        // Simulamos un request vacío o corrupto que rompa las restricciones de jakarta.validation en HistorialCitaRequest
        HistorialCitaRequest requestInvalido = new HistorialCitaRequest(); 

        mockMvc.perform(post("/api/gestion/historial-citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());
                
        // Verificamos que el controlador detuvo el flujo y nunca llamó al servicio de negocio
        Mockito.verify(historialCitaService, Mockito.times(0)).crearRegistro(any());
    }
}