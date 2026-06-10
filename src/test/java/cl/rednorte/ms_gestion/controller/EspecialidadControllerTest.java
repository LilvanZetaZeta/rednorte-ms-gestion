package cl.rednorte.ms_gestion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cl.rednorte.ms_gestion.entity.Especialidad;
import cl.rednorte.ms_gestion.service.EspecialidadService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EspecialidadController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva la seguridad para aislar el comportamiento del controlador
class EspecialidadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private EspecialidadService especialidadService;

    @Autowired
    private ObjectMapper objectMapper;

    private Especialidad especialidadMock;

    @BeforeEach
    void setUp() {
        especialidadMock = new Especialidad();
        especialidadMock.setId(10L);
        especialidadMock.setNombre("Traumatología");
    }

    @Test
    @DisplayName("POST /api/gestion/especialidades -> Debe retornar la especialidad creada")
    void create_DebeRetornarEspecialidadCreada() throws Exception {
        Mockito.when(especialidadService.crear(any(Especialidad.class))).thenReturn(especialidadMock);

        mockMvc.perform(post("/api/gestion/especialidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(especialidadMock)))
                .andExpect(status().isOk()) // Tu método retorna el objeto directo, por lo que Spring responde 200 OK
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.nombre").value("Traumatología"));
    }

    @Test
    @DisplayName("PUT /api/gestion/especialidades/{id} -> Debe retornar la especialidad actualizada envuelta en ResponseEntity")
    void update_DebeRetornarEspecialidadActualizada() throws Exception {
        Especialidad requestBody = new Especialidad();
        requestBody.setNombre("Traumatología Avanzada");

        Especialidad especialidadActualizada = new Especialidad();
        especialidadActualizada.setId(10L);
        especialidadActualizada.setNombre("Traumatología Avanzada");

        Mockito.when(especialidadService.actualizar(eq(10L), any(Especialidad.class))).thenReturn(especialidadActualizada);

        mockMvc.perform(put("/api/gestion/especialidades/{id}", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Traumatología Avanzada"));
    }

    @Test
    @DisplayName("DELETE /api/gestion/especialidades/{id} -> Debe eliminar y responder con No Content")
    void delete_DebeRetornarNoContent() throws Exception {
        Mockito.doNothing().when(especialidadService).eliminar(10L);

        mockMvc.perform(delete("/api/gestion/especialidades/{id}", 10L))
                .andExpect(status().isNoContent()); // Espera un 204

        Mockito.verify(especialidadService, Mockito.times(1)).eliminar(10L);
    }
}