package cl.rednorte.ms_gestion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cl.rednorte.ms_gestion.entity.CentroMedico;
import cl.rednorte.ms_gestion.service.CentroMedicoService;
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

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CentroMedicoController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva Spring Security para pruebas unitarias de controladores
class CentroMedicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private CentroMedicoService centroMedicoService;

    @Autowired
    private ObjectMapper objectMapper;

    private CentroMedico centroMock;

    @BeforeEach
    void setUp() {
        centroMock = new CentroMedico();
        centroMock.setId(1L);
        centroMock.setNombreSucursal("Clínica RedNorte Maipú");
        centroMock.setComuna("Maipú");
        centroMock.setDireccion("Av. Pajaritos 1234");
        centroMock.setRegion("Metropolitana");
    }

    @Test
    @DisplayName("POST /api/gestion/centros-medicos -> Debe crear un centro médico correctamente")
    void crearCentro_DebeRetornarCentroCreado() throws Exception {
        Mockito.when(centroMedicoService.crearCentro(any(CentroMedico.class))).thenReturn(centroMock);

        mockMvc.perform(post("/api/gestion/centros-medicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(centroMock)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombreSucursal").value("Clínica RedNorte Maipú"))
                .andExpect(jsonPath("$.comuna").value("Maipú"));
    }

    @Test
    @DisplayName("PUT /api/gestion/centros-medicos/{id} -> Debe actualizar completamente el centro médico")
    void actualizarCentro_DebeRetornarCentroActualizado() throws Exception {
        Mockito.when(centroMedicoService.actualizarCentro(eq(1L), any(CentroMedico.class))).thenReturn(centroMock);

        mockMvc.perform(put("/api/gestion/centros-medicos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(centroMock)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreSucursal").value("Clínica RedNorte Maipú"));
    }

    @Test
    @DisplayName("PATCH /api/gestion/centros-medicos/{id} -> Debe aplicar cambios parciales de forma exitosa")
    void parchearCentro_DebeRetornarCentroModificado() throws Exception {
        Map<String, Object> updates = new HashMap<>();
        updates.put("nombreSucursal", "Nuevo Nombre Sucursal");

        centroMock.setNombreSucursal("Nuevo Nombre Sucursal");
        Mockito.when(centroMedicoService.parchearCentro(eq(1L), eq(updates))).thenReturn(centroMock);

        mockMvc.perform(patch("/api/gestion/centros-medicos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreSucursal").value("Nuevo Nombre Sucursal"));
    }

    @Test
    @DisplayName("DELETE /api/gestion/centros-medicos/{id} -> Debe eliminar el centro y retornar No Content")
    void eliminarCentro_DebeRetornarNoContent() throws Exception {
        Mockito.doNothing().when(centroMedicoService).eliminarCentro(1L);

        mockMvc.perform(delete("/api/gestion/centros-medicos/{id}", 1L))
                .andExpect(status().isNoContent());

        Mockito.verify(centroMedicoService, Mockito.times(1)).eliminarCentro(1L);
    }

    @Test
    @DisplayName("POST /api/gestion/centros-medicos -> Debe fallar con 400 Bad Request cuando el payload es inválido")
    void crearCentro_PayloadInvalido_DebeRetornarBadRequest() throws Exception {
        // Simulamos un centro médico vacío que viola las reglas de @Valid
        CentroMedico centroInvalido = new CentroMedico(); 

        mockMvc.perform(post("/api/gestion/centros-medicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(centroInvalido)))
                .andExpect(status().isBadRequest());
    }
}