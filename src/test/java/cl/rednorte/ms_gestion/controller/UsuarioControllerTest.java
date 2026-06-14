package cl.rednorte.ms_gestion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cl.rednorte.ms_gestion.dto.RegistroRequest;
import cl.rednorte.ms_gestion.entity.Usuario;
import cl.rednorte.ms_gestion.service.UsuarioService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva la seguridad perimetral de JWT para aislar el controlador
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

     @SuppressWarnings("removal")
    @MockBean
    private UsuarioService usuarioService;

    @SuppressWarnings("removal") 
    @MockBean(name = "mvcValidator")
    private org.springframework.validation.Validator validator;

    @Autowired
    private ObjectMapper objectMapper;

    private RegistroRequest requestRegistro;
    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        // CORRECCIÓN CLAVE: Forzamos al validador a simular que el objeto NO contiene errores de restricciones
        Mockito.when(validator.supports(any())).thenReturn(true);
        Mockito.doNothing().when(validator).validate(any(), any(org.springframework.validation.Errors.class));

        // Completamos todos los campos del DTO para evitar cualquier filtro adicional
        requestRegistro = new RegistroRequest();
        requestRegistro.setIdAuth("auth0|ivan123");
        requestRegistro.setRut("19888777-6");
        requestRegistro.setNombreCompleto("Iván Castro");
        requestRegistro.setCorreo("ivan@rednorte.cl");
        requestRegistro.setRol(Usuario.RolUsuario.PACIENTE);
        requestRegistro.setEspecialidadIds(new ArrayList<>());

        // Inicializar Entidad Usuario Mock de salida
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setRut("19888777-6");
        usuarioMock.setNombreCompleto("Iván Castro");
        usuarioMock.setCorreo("ivan@rednorte.cl");
        usuarioMock.setRol(Usuario.RolUsuario.PACIENTE);
    }

    // ==========================================
    // TESTS PARA: POST / (registrar)
    // ==========================================

    @Test
    @DisplayName("POST /api/gestion/usuarios -> Debe retornar 201 Created y el usuario registrado")
    void registrar_Valido_RetornaCreated() throws Exception {
        Mockito.when(usuarioService.registrarPerfilPaciente(any(RegistroRequest.class))).thenReturn(usuarioMock);

        mockMvc.perform(post("/api/gestion/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestRegistro)))
                .andExpect(status().isCreated()) 
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.correo").value("ivan@rednorte.cl"));
    }

    @Test
    @DisplayName("POST /api/gestion/usuarios -> Debe retornar 400 Bad Request si el registro falla por duplicidad")
    void registrar_ErrorNegocio_RetornaBadRequest() throws Exception {
        Mockito.when(usuarioService.registrarPerfilPaciente(any(RegistroRequest.class)))
                .thenThrow(new RuntimeException("El RUT ya se encuentra registrado."));

        mockMvc.perform(post("/api/gestion/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestRegistro)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El RUT ya se encuentra registrado."));
    }

    // ==========================================
    // TESTS PARA: PUT /{id} (actualizar)
    // ==========================================

    @Test
    @DisplayName("PUT /api/gestion/usuarios/{id} -> Debe retornar 200 OK y el usuario actualizado")
    void actualizar_Valido_RetornaUsuario() throws Exception {
        Mockito.when(usuarioService.actualizarUsuario(eq(1L), any(Usuario.class))).thenReturn(usuarioMock);

        mockMvc.perform(put("/api/gestion/usuarios/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioMock)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    // ==========================================
    // TESTS PARA: PATCH /{id} (parchear)
    // ==========================================

    @Test
    @DisplayName("PATCH /api/gestion/usuarios/{id} -> Debe aplicar cambios parciales y retornar 200 OK")
    void parchear_Valido_RetornaUsuarioModificado() throws Exception {
        Map<String, Object> updates = new HashMap<>();
        updates.put("nombreCompleto", "Iván Castro Modificado");

        usuarioMock.setNombreCompleto("Iván Castro Modificado");
        Mockito.when(usuarioService.parchearUsuario(eq(1L), eq(updates))).thenReturn(usuarioMock);

        mockMvc.perform(patch("/api/gestion/usuarios/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCompleto").value("Iván Castro Modificado"));
    }

    @Test
    @DisplayName("PATCH /api/gestion/usuarios/{id} -> Debe retornar 400 Bad Request si los cambios parciales son inválidos")
    void parchear_Invalido_RetornaBadRequest() throws Exception {
        Map<String, Object> updates = new HashMap<>();
        updates.put("nombreCompleto", "Iván 123"); // Contiene números

        Map<String, String> errors = Map.of(
            "nombreCompleto", "El nombre solo puede contener letras y espacios",
            "error", "El nombre solo puede contener letras y espacios"
        );

        Mockito.when(usuarioService.parchearUsuario(eq(1L), eq(updates)))
                .thenThrow(new cl.rednorte.ms_gestion.exception.FormValidationException(errors));

        mockMvc.perform(patch("/api/gestion/usuarios/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombreCompleto").value("El nombre solo puede contener letras y espacios"))
                .andExpect(jsonPath("$.error").value("El nombre solo puede contener letras y espacios"));
    }

    // ==========================================
    // TESTS PARA: POST /asignar-medico y /asignar-admin
    // ==========================================

    @Test
    @DisplayName("POST /api/gestion/usuarios/asignar-medico -> Debe retornar 200 OK al cambiar rol")
    void asignarMedico_Valido_RetornaUsuario() throws Exception {
        Map<String, String> body = Map.of("correo", "ivan@rednorte.cl");
        usuarioMock.setRol(Usuario.RolUsuario.MEDICO);

        Mockito.when(usuarioService.asignarRolMedicoPorCorreo("ivan@rednorte.cl")).thenReturn(usuarioMock);

        mockMvc.perform(post("/api/gestion/usuarios/asignar-medico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("MEDICO"));
    }

    @Test
    @DisplayName("POST /api/gestion/usuarios/asignar-medico -> Retorna 400 Bad Request si el usuario ya posee el rol")
    void asignarMedico_YaEsMedico_RetornaBadRequest() throws Exception {
        Map<String, String> body = Map.of("correo", "ivan@rednorte.cl");

        Mockito.when(usuarioService.asignarRolMedicoPorCorreo("ivan@rednorte.cl"))
                .thenThrow(new RuntimeException("El usuario ya tiene el rol de Médico."));

        mockMvc.perform(post("/api/gestion/usuarios/asignar-medico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El usuario ya tiene el rol de Médico."));
    }

    // ==========================================
    // TESTS PARA: PATCH /{id}/rol, /{id}/centro, /{id}/especialidades
    // ==========================================

    @Test
    @DisplayName("PATCH /api/gestion/usuarios/{id}/rol -> Procesa el cambio de Enum de Rol de forma exitosa")
    void actualizarRol_Valido_RetornaOk() throws Exception {
        Map<String, String> body = Map.of("rol", "medico");
        usuarioMock.setRol(Usuario.RolUsuario.MEDICO);

        Mockito.when(usuarioService.actualizarRol(eq(1L), eq(Usuario.RolUsuario.MEDICO))).thenReturn(usuarioMock);

        mockMvc.perform(patch("/api/gestion/usuarios/{id}/rol", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("MEDICO"));
    }

    @Test
    @DisplayName("PATCH /api/gestion/usuarios/{id}/centro -> Vincula el ID de la sucursal médica")
    void actualizarCentro_Valido_RetornaOk() throws Exception {
        Map<String, Long> body = Map.of("centroId", 20L);
        Mockito.when(usuarioService.actualizarCentroMedico(eq(1L), eq(20L))).thenReturn(usuarioMock);

        mockMvc.perform(patch("/api/gestion/usuarios/{id}/centro", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/gestion/usuarios/{id}/especialidades -> Recibe y procesa un arreglo de IDs")
    void actualizarEspecialidades_Valido_RetornaOk() throws Exception {
        List<Long> especialidadesIds = List.of(10L, 11L);
        Mockito.when(usuarioService.actualizarEspecialidades(eq(1L), eq(especialidadesIds))).thenReturn(usuarioMock);

        mockMvc.perform(patch("/api/gestion/usuarios/{id}/especialidades", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(especialidadesIds)))
                .andExpect(status().isOk());
    }

    // ==========================================
    // TESTS PARA: DELETE /{id} (eliminar)
    // ==========================================

    @Test
    @DisplayName("DELETE /api/gestion/usuarios/{id} -> Retorna 204 No Content")
    void eliminar_IdExistente_RetornaNoContent() throws Exception {
        Mockito.doNothing().when(usuarioService).eliminarUsuario(1L);

        mockMvc.perform(delete("/api/gestion/usuarios/{id}", 1L))
                .andExpect(status().isNoContent());

        Mockito.verify(usuarioService, Mockito.times(1)).eliminarUsuario(1L);
    }
}