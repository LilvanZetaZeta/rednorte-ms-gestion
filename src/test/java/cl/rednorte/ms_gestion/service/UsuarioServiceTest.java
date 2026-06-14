package cl.rednorte.ms_gestion.service;

import cl.rednorte.ms_gestion.dto.RegistroRequest;
import cl.rednorte.ms_gestion.entity.CentroMedico;
import cl.rednorte.ms_gestion.entity.Especialidad;
import cl.rednorte.ms_gestion.entity.Usuario;
import cl.rednorte.ms_gestion.repository.CentroMedicoRepository;
import cl.rednorte.ms_gestion.repository.EspecialidadRepository;
import cl.rednorte.ms_gestion.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private EspecialidadRepository especialidadRepository;
    @Mock private CentroMedicoRepository centroMedicoRepository;

    @InjectMocks private UsuarioService service;

    private Usuario usuarioBase;
    private Especialidad especialidadMock;
    private CentroMedico centroMock;
    private RegistroRequest requestRegistro;

    @BeforeEach
    void setUp() {
        usuarioBase = new Usuario();
        usuarioBase.setId(1L);
        usuarioBase.setRut("19888777-6");
        usuarioBase.setNombreCompleto("Iván Castro");
        usuarioBase.setCorreo("ivan@rednorte.cl");
        usuarioBase.setRol(Usuario.RolUsuario.PACIENTE);
        usuarioBase.setIdAuth("auth0|ivan123");

        especialidadMock = new Especialidad();
        especialidadMock.setId(10L);
        especialidadMock.setNombre("Cardiología");

        centroMock = new CentroMedico();
        centroMock.setId(20L);
        centroMock.setNombreSucursal("Sucursal Pudahuel");

        requestRegistro = new RegistroRequest();
        requestRegistro.setIdAuth("auth0|ivan123");
        requestRegistro.setRut("19888777-6");
        requestRegistro.setNombreCompleto("Iván Castro");
        requestRegistro.setCorreo("ivan@rednorte.cl");
    }

    // ==========================================
    // SECCIÓN: registrarPerfilPaciente()
    // ==========================================

    @Test
    @DisplayName("registrarPerfilPaciente -> Debe guardar exitosamente si RUT y Correo son únicos")
    void registrar_CaminoFeliz_GuardaCorrectamente() {
        Mockito.when(usuarioRepository.existsByCorreo(requestRegistro.getCorreo())).thenReturn(false);
        Mockito.when(usuarioRepository.existsByRut(requestRegistro.getRut())).thenReturn(false);
        Mockito.when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioBase);

        Usuario resultado = service.registrarPerfilPaciente(requestRegistro);

        assertNotNull(resultado);
        assertEquals(Usuario.RolUsuario.PACIENTE, resultado.getRol()); // Rol por defecto
        Mockito.verify(usuarioRepository, Mockito.times(1)).save(any());
    }

    @Test
    @DisplayName("registrarPerfilPaciente -> Debe lanzar excepción si el correo ya existe")
    void registrar_CorreoDuplicado_LanzaException() {
        Mockito.when(usuarioRepository.existsByCorreo(requestRegistro.getCorreo())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.registrarPerfilPaciente(requestRegistro));
        Mockito.verify(usuarioRepository, Mockito.times(0)).save(any());
    }

    @Test
    @DisplayName("registrarPerfilPaciente -> Médico: Valida y asigna especialidades existentes")
    void registrar_RolMedicoConEspecialidades_AsignaYGuarda() {
        requestRegistro.setRol(Usuario.RolUsuario.MEDICO);
        requestRegistro.setEspecialidadIds(List.of(10L));
        usuarioBase.setRol(Usuario.RolUsuario.MEDICO);

        Mockito.when(usuarioRepository.existsByCorreo(any())).thenReturn(false);
        Mockito.when(usuarioRepository.existsByRut(any())).thenReturn(false);
        Mockito.when(especialidadRepository.findAllById(List.of(10L))).thenReturn(List.of(especialidadMock));
        Mockito.when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioBase);

        Usuario resultado = service.registrarPerfilPaciente(requestRegistro);

        assertNotNull(resultado);
        Mockito.verify(especialidadRepository, Mockito.times(1)).findAllById(any());
    }

    // ==========================================
    // SECCIÓN: Modificaciones parciales y totales
    // ==========================================

    @Test
    @DisplayName("actualizarUsuario -> Debe lanzar excepción si intenta cambiar al correo de otro usuario")
    void actualizar_CorreoEnUsoPorOtro_LanzaException() {
        Usuario reqUpdate = new Usuario();
        reqUpdate.setCorreo("cambio@correo.cl"); // Cambia de ivan@rednorte.cl a otro

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));
        Mockito.when(usuarioRepository.existsByCorreo("cambio@correo.cl")).thenReturn(true); // Ocupado

        assertThrows(IllegalArgumentException.class, () -> service.actualizarUsuario(1L, reqUpdate));
        Mockito.verify(usuarioRepository, Mockito.times(0)).save(any());
    }

    @Test
    @DisplayName("parchearUsuario -> Aplica cambios parciales dinámicamente mapeando el mapa")
    void parchear_SoloNombre_ModificaExitosamente() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("nombreCompleto", "Iván Castro Modificado");

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));
        Mockito.when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = service.parchearUsuario(1L, updates);

        assertEquals("Iván Castro Modificado", resultado.getNombreCompleto());
        assertEquals("ivan@rednorte.cl", resultado.getCorreo()); // Mantiene original
    }

    @Test
    @DisplayName("parchearUsuario -> Lanza FormValidationException si el nombre contiene números")
    void parchear_NombreInvalido_LanzaFormValidationException() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("nombreCompleto", "Iván 123");

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));

        cl.rednorte.ms_gestion.exception.FormValidationException exception = assertThrows(
            cl.rednorte.ms_gestion.exception.FormValidationException.class,
            () -> service.parchearUsuario(1L, updates)
        );

        assertTrue(exception.getErrors().containsKey("nombreCompleto"));
        assertEquals("El nombre solo puede contener letras y espacios", exception.getErrors().get("nombreCompleto"));
    }

    @Test
    @DisplayName("parchearUsuario -> Lanza FormValidationException si el correo es inválido")
    void parchear_CorreoInvalido_LanzaFormValidationException() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("correo", "correo_invalido");

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));

        cl.rednorte.ms_gestion.exception.FormValidationException exception = assertThrows(
            cl.rednorte.ms_gestion.exception.FormValidationException.class,
            () -> service.parchearUsuario(1L, updates)
        );

        assertTrue(exception.getErrors().containsKey("correo"));
        assertEquals("Por favor ingresa un correo válido (ej: usuario@ejemplo.com)", exception.getErrors().get("correo"));
    }

    // ==========================================
    // SECCIÓN: Control de Roles y Flujos de Estado
    // ==========================================

    @Test
    @DisplayName("asignarRolMedicoPorCorreo -> Cambia rol a MEDICO con éxito")
    void asignarMedico_UsuarioExistente_CambiaRol() {
        Mockito.when(usuarioRepository.findByCorreo("ivan@rednorte.cl")).thenReturn(Optional.of(usuarioBase));
        Mockito.when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = service.asignarRolMedicoPorCorreo("ivan@rednorte.cl");

        assertEquals(Usuario.RolUsuario.MEDICO, resultado.getRol());
    }

    @Test
    @DisplayName("asignarRolMedicoPorCorreo -> Lanza IllegalStateException si ya era médico")
    void asignarMedico_YaEsMedico_LanzaException() {
        usuarioBase.setRol(Usuario.RolUsuario.MEDICO);
        Mockito.when(usuarioRepository.findByCorreo("ivan@rednorte.cl")).thenReturn(Optional.of(usuarioBase));

        assertThrows(IllegalStateException.class, () -> service.asignarRolMedicoPorCorreo("ivan@rednorte.cl"));
        Mockito.verify(usuarioRepository, Mockito.times(0)).save(any());
    }

    // ==========================================
    // SECCIÓN: Asignaciones Relacionales Especiales
    // ==========================================

    @Test
    @DisplayName("actualizarCentroMedico -> Desvincula el centro médico si se envía id nulo")
    void actualizarCentro_IdNulo_RemueveCentro() {
        usuarioBase.setCentroMedico(centroMock); // Inicia con centro
        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));
        Mockito.when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = service.actualizarCentroMedico(1L, null);

        assertNull(resultado.getCentroMedico());
    }

    @Test
    @DisplayName("actualizarEspecialidades -> Lanza excepción si el usuario no tiene rol MEDICO")
    void actualizarEspecialidades_UsuarioNoEsMedico_LanzaException() {
        // usuarioBase tiene rol PACIENTE por defecto
        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));

        assertThrows(IllegalStateException.class, () -> service.actualizarEspecialidades(1L, List.of(10L)));
        Mockito.verify(especialidadRepository, Mockito.times(0)).findAllById(any());
    }

    @Test
    @DisplayName("actualizarEspecialidades -> Lanza excepción si alguna de las especialidades del arreglo no existe")
    void actualizarEspecialidades_EspecialidadInexistente_LanzaException() {
        usuarioBase.setRol(Usuario.RolUsuario.MEDICO); // Forzamos rol válido
        List<Long> idsConsultados = List.of(10L, 11L); // Solicitamos dos IDs

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));
        // El repositorio solo encuentra una especialidad válida de las dos solicitadas
        Mockito.when(especialidadRepository.findAllById(idsConsultados)).thenReturn(List.of(especialidadMock));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            service.actualizarEspecialidades(1L, idsConsultados)
        );

        assertTrue(exception.getMessage().contains("no existen en el sistema"));
        Mockito.verify(usuarioRepository, Mockito.times(0)).save(any());
    }
}