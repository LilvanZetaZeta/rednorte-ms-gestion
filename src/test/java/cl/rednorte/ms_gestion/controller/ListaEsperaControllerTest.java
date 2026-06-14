package cl.rednorte.ms_gestion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cl.rednorte.ms_gestion.dto.ListaEsperaRequest;
import cl.rednorte.ms_gestion.entity.CentroMedico;
import cl.rednorte.ms_gestion.entity.ListaEsperaLocal;
import cl.rednorte.ms_gestion.entity.Usuario;
import cl.rednorte.ms_gestion.service.ListaEsperaLocalService;
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

@WebMvcTest(ListaEsperaController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva la seguridad perimetral para aislar el controlador
class ListaEsperaControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @SuppressWarnings("removal")
    @MockBean
    private ListaEsperaLocalService listaEsperaService;

    @Autowired
    private ObjectMapper objectMapper;

    private ListaEsperaRequest requestValido;
    private ListaEsperaLocal listaEsperaMock;

    @BeforeEach
    void setUp() {
        // Mock de datos básicos de relación
        CentroMedico centro = new CentroMedico();
        centro.setId(1L);
        centro.setNombreSucursal("Sucursal Pudahuel");

        Usuario paciente = new Usuario();
        paciente.setId(10L);
        paciente.setNombreCompleto("Iván Castro");

        // Mock del DTO Request
        requestValido = new ListaEsperaRequest();
        requestValido.setCentroId(1L);
        requestValido.setPacienteId(10L);
        requestValido.setPrioridad(2);
        requestValido.setEspecialidad("Traumatología");

        // Mock de la Entidad de Respuesta
        listaEsperaMock = new ListaEsperaLocal();
        listaEsperaMock.setId(50L);
        listaEsperaMock.setCentro(centro);
        listaEsperaMock.setPaciente(paciente);
        listaEsperaMock.setPrioridad(2);
        listaEsperaMock.setEspecialidad("Traumatología");
    }

    // ==========================================
    // PRUEBAS PARA: POST / (create)
    // ==========================================

    @Test
    @DisplayName("POST /api/gestion/lista-espera -> Debe retornar 200 OK y el registro creado")
    void create_PayloadValido_RetornaRegistro() throws Exception {
        Mockito.when(listaEsperaService.crear(any(ListaEsperaRequest.class))).thenReturn(listaEsperaMock);

        mockMvc.perform(post("/api/gestion/lista-espera")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(50L))
                .andExpect(jsonPath("$.prioridad").value(2))
                .andExpect(jsonPath("$.especialidad").value("Traumatología"))
                .andExpect(jsonPath("$.centro.nombreSucursal").value("Sucursal Pudahuel"));
    }

    @Test
    @DisplayName("POST /api/gestion/lista-espera -> Debe retornar 400 Bad Request si viola @Valid en creación")
    void create_PayloadInvalido_RetornaBadRequest() throws Exception {
        ListaEsperaRequest requestInvalido = new ListaEsperaRequest(); // Payload vacío que viola validaciones

        mockMvc.perform(post("/api/gestion/lista-espera")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        Mockito.verify(listaEsperaService, Mockito.times(0)).crear(any());
    }

    // ==========================================
    // PRUEBAS PARA: PUT /{id} (update)
    // ==========================================

    @Test
    @DisplayName("PUT /api/gestion/lista-espera/{id} -> Debe retornar 200 OK y el registro actualizado")
    void update_PayloadValido_RetornaRegistroActualizado() throws Exception {
        requestValido.setPrioridad(5); // Supongamos que sube la prioridad
        listaEsperaMock.setPrioridad(5);

        Mockito.when(listaEsperaService.actualizar(eq(50L), any(ListaEsperaRequest.class))).thenReturn(listaEsperaMock);

        mockMvc.perform(put("/api/gestion/lista-espera/{id}", 50L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(50L))
                .andExpect(jsonPath("$.prioridad").value(5));
    }

    @Test
    @DisplayName("PUT /api/gestion/lista-espera/{id} -> Debe retornar 400 Bad Request si viola @Valid en actualización")
    void update_PayloadInvalido_RetornaBadRequest() throws Exception {
        ListaEsperaRequest requestInvalido = new ListaEsperaRequest();

        mockMvc.perform(put("/api/gestion/lista-espera/{id}", 50L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        Mockito.verify(listaEsperaService, Mockito.times(0)).actualizar(any(), any());
    }

    // ==========================================
    // PRUEBAS PARA: DELETE /{id} (delete)
    // ==========================================

    @Test
    @DisplayName("DELETE /api/gestion/lista-espera/{id} -> Debe retornar 204 No Content al eliminar")
    void delete_IdExistente_RetornaNoContent() throws Exception {
        Mockito.doNothing().when(listaEsperaService).eliminar(50L);

        mockMvc.perform(delete("/api/gestion/lista-espera/{id}", 50L))
                .andExpect(status().isNoContent()); // Valida el HTTP 204

        Mockito.verify(listaEsperaService, Mockito.times(1)).eliminar(50L);
    }
}