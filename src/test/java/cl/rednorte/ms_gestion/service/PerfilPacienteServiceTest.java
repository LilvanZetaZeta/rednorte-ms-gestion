package cl.rednorte.ms_gestion.service;

import cl.rednorte.ms_gestion.dto.PerfilPacienteRequest;
import cl.rednorte.ms_gestion.entity.PerfilPaciente;
import cl.rednorte.ms_gestion.entity.Usuario;
import cl.rednorte.ms_gestion.repository.PerfilPacienteRepository;
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
class PerfilPacienteServiceTest {

    @Mock
    private PerfilPacienteRepository perfilRepo;

    @Mock
    private UsuarioRepository usuarioRepo;

    @InjectMocks
    private PerfilPacienteService service;

    private Usuario usuarioMock;
    private PerfilPaciente perfilMock;
    private PerfilPacienteRequest requestMock;
    private final String AUTH_ID_TEST = "auth0|6543210abc";

    @BeforeEach
    void setUp() {
        // Mock de Usuario (Entidad Base)
        usuarioMock = new Usuario();
        usuarioMock.setId(55L);
        usuarioMock.setNombreCompleto("Iván Castro");

        // Mock de DTO Request
        requestMock = new PerfilPacienteRequest();
        requestMock.setIdAuth(AUTH_ID_TEST);
        requestMock.setPrevision("FONASA");
        requestMock.setTelefonoContacto("+56912345678");

        // Mock de PerfilPaciente finalizado
        perfilMock = new PerfilPaciente();
        perfilMock.setId(1L);
        perfilMock.setPacienteId(55L);
        perfilMock.setIdAuth(AUTH_ID_TEST);
        perfilMock.setPrevision("FONASA");
        perfilMock.setTelefonoContacto("+56912345678");
    }

    // ==========================================
    // PRUEBAS: crearPerfil
    // ==========================================

    @Test
    @DisplayName("crearPerfil -> Debe guardar y retornar el perfil si el usuario existe y no tiene perfil previo")
    void crearPerfil_Valido_GuardaYRetornaPerfil() {
        // Arrange
        Mockito.when(usuarioRepo.findByIdAuth(AUTH_ID_TEST)).thenReturn(Optional.of(usuarioMock));
        Mockito.when(perfilRepo.findByIdAuth(AUTH_ID_TEST)).thenReturn(Optional.empty()); // No hay duplicados
        Mockito.when(perfilRepo.save(any(PerfilPaciente.class))).thenReturn(perfilMock);

        // Act
        PerfilPaciente resultado = service.crearPerfil(requestMock);

        // Assert
        assertNotNull(resultado);
        assertEquals(55L, resultado.getPacienteId());
        assertEquals(AUTH_ID_TEST, resultado.getIdAuth());
        assertEquals("FONASA", resultado.getPrevision());
        
        Mockito.verify(usuarioRepo, Mockito.times(1)).findByIdAuth(AUTH_ID_TEST);
        Mockito.verify(perfilRepo, Mockito.times(1)).findByIdAuth(AUTH_ID_TEST);
        Mockito.verify(perfilRepo, Mockito.times(1)).save(any(PerfilPaciente.class));
    }

    @Test
    @DisplayName("crearPerfil -> Debe lanzar EntityNotFoundException si el idAuth no corresponde a ningún usuario")
    void crearPerfil_UsuarioNoExiste_LanzaException() {
        // Arrange
        Mockito.when(usuarioRepo.findByIdAuth(AUTH_ID_TEST)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            service.crearPerfil(requestMock);
        });

        assertEquals("Usuario no encontrado con el idAuth proporcionado", exception.getMessage());
        
        // Verificaciones de contención (falla rápido)
        Mockito.verify(perfilRepo, Mockito.times(0)).findByIdAuth(any());
        Mockito.verify(perfilRepo, Mockito.times(0)).save(any());
    }

    @Test
    @DisplayName("crearPerfil -> Debe lanzar IllegalStateException si el idAuth ya cuenta con un perfil existente")
    void crearPerfil_PerfilYaExistente_LanzaException() {
        // Arrange
        Mockito.when(usuarioRepo.findByIdAuth(AUTH_ID_TEST)).thenReturn(Optional.of(usuarioMock));
        Mockito.when(perfilRepo.findByIdAuth(AUTH_ID_TEST)).thenReturn(Optional.of(perfilMock)); // Ya existe un perfil

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            service.crearPerfil(requestMock);
        });

        assertEquals("El paciente ya tiene un perfil configurado.", exception.getMessage());
        
        // Jamás debe llegar a la persistencia del save
        Mockito.verify(perfilRepo, Mockito.times(0)).save(any());
    }

    // ==========================================
    // PRUEBAS: actualizarPerfil
    // ==========================================

    @Test
    @DisplayName("actualizarPerfil -> Debe modificar campos de contacto y previsión exitosamente")
    void actualizarPerfil_Existe_ModificaYGuarda() {
        // Arrange
        PerfilPacienteRequest reqUpdate = new PerfilPacienteRequest();
        reqUpdate.setPrevision("ISAPRE BANMEDICA");
        reqUpdate.setTelefonoContacto("+56988887777");

        Mockito.when(perfilRepo.findById(1L)).thenReturn(Optional.of(perfilMock));
        Mockito.when(perfilRepo.save(any(PerfilPaciente.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        PerfilPaciente resultado = service.actualizarPerfil(1L, reqUpdate);

        // Assert
        assertNotNull(resultado);
        assertEquals("ISAPRE BANMEDICA", resultado.getPrevision());
        assertEquals("+56988887777", resultado.getTelefonoContacto());
        assertEquals(55L, resultado.getPacienteId()); // Asegura que no se corrompe la relación base
        
        Mockito.verify(perfilRepo, Mockito.times(1)).save(any());
    }

    @Test
    @DisplayName("actualizarPerfil -> Debe lanzar EntityNotFoundException si el ID del perfil no existe")
    void actualizarPerfil_NoExiste_LanzaException() {
        // Arrange
        Mockito.when(perfilRepo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            service.actualizarPerfil(999L, requestMock);
        });

        Mockito.verify(perfilRepo, Mockito.times(0)).save(any());
    }
}