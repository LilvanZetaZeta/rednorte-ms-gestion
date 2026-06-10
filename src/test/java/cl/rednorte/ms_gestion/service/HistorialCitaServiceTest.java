package cl.rednorte.ms_gestion.service;

import cl.rednorte.ms_gestion.dto.HistorialCitaRequest;
import cl.rednorte.ms_gestion.entity.HistorialCita;
import cl.rednorte.ms_gestion.entity.Usuario;
import cl.rednorte.ms_gestion.repository.HistorialCitaRepository;
import cl.rednorte.ms_gestion.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class HistorialCitaServiceTest {

    @Mock
    private HistorialCitaRepository historialRepo;

    @Mock
    private UsuarioRepository usuarioRepo;

    @InjectMocks
    private HistorialCitaService service;

    private Usuario pacienteMock;
    private Usuario medicoMock;
    private HistorialCitaRequest requestValido;

    @BeforeEach
    void setUp() {
        // Inicializar Paciente Mock
        pacienteMock = new Usuario();
        pacienteMock.setId(10L);
        pacienteMock.setNombreCompleto("Juan Pérez (Paciente)");

        // Inicializar Médico Mock
        medicoMock = new Usuario();
        medicoMock.setId(20L);
        medicoMock.setNombreCompleto("Dra. María González (Médico)");

        // Inicializar el Request DTO con el que probaremos
        requestValido = new HistorialCitaRequest();
        requestValido.setPacienteId(10L);
        requestValido.setMedicoId(20L);
        requestValido.setReservaId(100L);
        requestValido.setObservaciones("Paciente evoluciona favorablemente del tratamiento.");
        requestValido.setProcedimientoRealizado("Control general de rutina");
    }

    // ==========================================
    // PRUEBAS CAMINO FELIZ (HAPPY PATH)
    // ==========================================

    @Test
    @DisplayName("crearRegistro -> Debe registrar el historial exitosamente si el paciente y el médico existen")
    void crearRegistro_DatosValidos_GuardaYRetornaHistorial() {
        // Arrange (Configurar respuestas de los mocks)
        Mockito.when(usuarioRepo.findById(10L)).thenReturn(Optional.of(pacienteMock));
        Mockito.when(usuarioRepo.findById(20L)).thenReturn(Optional.of(medicoMock));
        
        Mockito.when(historialRepo.save(any(HistorialCita.class))).thenAnswer(invocation -> {
            HistorialCita h = invocation.getArgument(0);
            h.setId(1L); // Simulamos que la BD le asigna un ID autoincremental
            return h;
        });

        // Act (Ejecutar el método a testear)
        HistorialCita resultado = service.crearRegistro(requestValido);

        // Assert (Verificar los resultados obtenidos)
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(10L, resultado.getPaciente().getId());
        assertEquals(20L, resultado.getMedico().getId());
        assertEquals(100L, resultado.getReservaId());
        assertEquals("Control general de rutina", resultado.getProcedimientoRealizado());
        assertNotNull(resultado.getFechaAtencion()); // Verifica que se asignó LocalDateTime.now()

        // Verificaciones de comportamiento
        Mockito.verify(usuarioRepo, Mockito.times(2)).findById(any());
        Mockito.verify(historialRepo, Mockito.times(1)).save(any(HistorialCita.class));
    }

    // ==========================================
    // PRUEBAS DE CASOS BORDE (EDGE CASES)
    // ==========================================

    @Test
    @DisplayName("crearRegistro -> Debe lanzar EntityNotFoundException si el paciente no existe")
    void crearRegistro_PacienteNoExiste_LanzaException() {
        // Arrange: El repositorio de usuarios no encuentra al paciente
        Mockito.when(usuarioRepo.findById(10L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            service.crearRegistro(requestValido);
        });

        assertEquals("Paciente no encontrado", exception.getMessage());
        
        // Verificación de seguridad: Si falla el paciente, el código debe abortar de inmediato
        // Por ende, nunca debe intentar buscar al médico ni guardar en el historial repo
        Mockito.verify(usuarioRepo, Mockito.times(1)).findById(10L);
        Mockito.verify(usuarioRepo, Mockito.times(0)).findById(20L); 
        Mockito.verify(historialRepo, Mockito.times(0)).save(any(HistorialCita.class));
    }

    @Test
    @DisplayName("crearRegistro -> Debe lanzar EntityNotFoundException si el paciente existe pero el médico no")
    void crearRegistro_MedicoNoExiste_LanzaException() {
        // Arrange: El paciente sí existe, pero el médico no
        Mockito.when(usuarioRepo.findById(10L)).thenReturn(Optional.of(pacienteMock));
        Mockito.when(usuarioRepo.findById(20L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            service.crearRegistro(requestValido);
        });

        assertEquals("Médico no encontrado", exception.getMessage());

        // Verificación de seguridad
        Mockito.verify(usuarioRepo, Mockito.times(1)).findById(10L);
        Mockito.verify(usuarioRepo, Mockito.times(1)).findById(20L);
        Mockito.verify(historialRepo, Mockito.times(0)).save(any(HistorialCita.class));
    }
}