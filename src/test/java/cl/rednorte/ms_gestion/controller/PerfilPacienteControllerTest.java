package cl.rednorte.ms_gestion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cl.rednorte.ms_gestion.dto.PerfilPacienteRequest;
import cl.rednorte.ms_gestion.entity.PerfilPaciente;
import cl.rednorte.ms_gestion.service.PerfilPacienteService;
import jakarta.persistence.EntityNotFoundException;
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

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PerfilPacienteController.class)
@AutoConfigureMockMvc(addFilters = false) // Remueve filtros de JWT para aislar el comportamiento del controlador
class PerfilPacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private PerfilPacienteService perfilPacienteService;

    @Autowired
    private ObjectMapper objectMapper;

    private PerfilPacienteRequest requestMock;
    private PerfilPaciente perfilMock;
    private final String AUTH_ID_TEST = "auth0|ivan123";

    @BeforeEach
    void setUp() {
        // Inicializar Request DTO
        requestMock = new PerfilPacienteRequest();
        requestMock.setIdAuth(AUTH_ID_TEST);
        requestMock.setPrevision("FONASA");
        requestMock.setTelefonoContacto("+56912345678");

        // Inicializar Entidad de Respuesta
        perfilMock = new PerfilPaciente();
        perfilMock.setId(1L);
        perfilMock.setPacienteId(55L);
        perfilMock.setIdAuth(AUTH_ID_TEST);
        perfilMock.setPrevision("FONASA");
        perfilMock.setTelefonoContacto("+56912345678");
    }

    // ==========================================
    // PRUEBAS PARA: POST / (crear)
    // ==========================================

    @Test
    @DisplayName("POST /api/gestion/perfil-pacientes -> Debe retornar 200 OK y el perfil clínico creado")
    void crear_PayloadValido_RetornaPerfilCreado() throws Exception {
        Mockito.when(perfilPacienteService.crearPerfil(any(PerfilPacienteRequest.class))).thenReturn(perfilMock);

        mockMvc.perform(post("/api/gestion/perfil-pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestMock)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.pacienteId").value(55L))
                .andExpect(jsonPath("$.idAuth").value(AUTH_ID_TEST))
                .andExpect(jsonPath("$.prevision").value("FONASA"))
                .andExpect(jsonPath("$.telefonoContacto").value("+56912345678"));
    }

    // ==========================================
    // PRUEBAS PARA: PUT /{id} (actualizar)
    // ==========================================

    @Test
    @DisplayName("PUT /api/gestion/perfil-pacientes/{id} -> Debe retornar 200 OK y el perfil modificado")
    void actualizar_PerfilExistente_RetornaPerfilActualizado() throws Exception {
        requestMock.setPrevision("ISAPRE");
        perfilMock.setPrevision("ISAPRE");

        Mockito.when(perfilPacienteService.actualizarPerfil(eq(1L), any(PerfilPacienteRequest.class))).thenReturn(perfilMock);

        mockMvc.perform(put("/api/gestion/perfil-pacientes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestMock)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.prevision").value("ISAPRE"));
    }

    @Test
    @DisplayName("PUT /api/gestion/perfil-pacientes/{id} -> Debe manejar correctamente si el perfil no existe")
    void actualizar_PerfilInexistente_ManejaExcepcion() throws Exception {
        Mockito.when(perfilPacienteService.actualizarPerfil(eq(999L), any(PerfilPacienteRequest.class)))
                .thenThrow(new EntityNotFoundException("Perfil no encontrado"));

        try {
            // Se ejecuta la petición HTTP simulada
            mockMvc.perform(put("/api/gestion/perfil-pacientes/{id}", 999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestMock)))
                    .andExpect(status().is4xxClientError()); // Pasa si tienes un @ControllerAdvice (404)
        } catch (Exception e) {
            // Si no tienes un Handler, la excepción burbujea. El bloque catch la captura
            // y valida que la causa raíz sea efectivamente la ausencia de la entidad.
            assertNotNull(e.getCause(), "La petición lanzó una excepción sin un manejador global configurado");
            assertInstanceOf(EntityNotFoundException.class, e.getCause());
        }
    }
}