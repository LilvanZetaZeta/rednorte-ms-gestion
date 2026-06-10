package cl.rednorte.ms_gestion.service;

import cl.rednorte.ms_gestion.dto.ListaEsperaRequest;
import cl.rednorte.ms_gestion.entity.CentroMedico;
import cl.rednorte.ms_gestion.entity.ListaEsperaLocal;
import cl.rednorte.ms_gestion.entity.Usuario;
import cl.rednorte.ms_gestion.repository.CentroMedicoRepository;
import cl.rednorte.ms_gestion.repository.ListaEsperaLocalRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ListaEsperaLocalServiceTest {

    @Mock private ListaEsperaLocalRepository repository;
    @Mock private CentroMedicoRepository centroRepo;
    @Mock private UsuarioRepository usuarioRepo;

    @InjectMocks private ListaEsperaLocalService service;

    private CentroMedico centroMock;
    private Usuario pacienteMock;
    private ListaEsperaRequest requestValido;
    private ListaEsperaLocal listaEsperaMock;

    @BeforeEach
    void setUp() {
        centroMock = new CentroMedico();
        centroMock.setId(1L);
        centroMock.setNombreSucursal("Centro Médico Pudahuel");

        pacienteMock = new Usuario();
        pacienteMock.setId(10L);
        pacienteMock.setNombreCompleto("Iván Castro");

        requestValido = new ListaEsperaRequest();
        requestValido.setCentroId(1L);
        requestValido.setPacienteId(10L);
        requestValido.setPrioridad(1);
        requestValido.setEspecialidad("Traumatología");

        listaEsperaMock = new ListaEsperaLocal();
        listaEsperaMock.setId(100L);
        listaEsperaMock.setCentro(centroMock);
        listaEsperaMock.setPaciente(pacienteMock);
        listaEsperaMock.setPrioridad(1);
        listaEsperaMock.setEspecialidad("Traumatología");
    }

    // ==========================================
    // PRUEBAS: obtenerPorId
    // ==========================================

    @Test
    @DisplayName("obtenerPorId -> Debe retornar el registro si existe")
    void obtenerPorId_Existe_RetornaRegistro() {
        Mockito.when(repository.findById(100L)).thenReturn(Optional.of(listaEsperaMock));

        ListaEsperaLocal resultado = service.obtenerPorId(100L);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
    }

    @Test
    @DisplayName("obtenerPorId -> Debe lanzar EntityNotFoundException si no existe")
    void obtenerPorId_NoExiste_LanzaException() {
        Mockito.when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.obtenerPorId(999L));
    }

    // ==========================================
    // PRUEBAS: crear (Camino Feliz y Casos Borde)
    // ==========================================

    @Test
    @DisplayName("crear -> Debe guardar exitosamente si cumple todas las reglas")
    void crear_Valido_GuardaYRetornaRegistro() {
        Mockito.when(centroRepo.findById(1L)).thenReturn(Optional.of(centroMock));
        Mockito.when(usuarioRepo.findById(10L)).thenReturn(Optional.of(pacienteMock));
        Mockito.when(repository.findByPacienteId(10L)).thenReturn(new ArrayList<>()); // Ninguna inscripción previa
        Mockito.when(repository.save(any(ListaEsperaLocal.class))).thenReturn(listaEsperaMock);

        ListaEsperaLocal resultado = service.crear(requestValido);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        Mockito.verify(repository, Mockito.times(1)).save(any(ListaEsperaLocal.class));
    }

    @Test
    @DisplayName("crear -> Debe lanzar EntityNotFoundException si el centro médico no existe")
    void crear_CentroNoExiste_LanzaException() {
        Mockito.when(centroRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.crear(requestValido));
        Mockito.verify(repository, Mockito.times(0)).save(any());
    }

    @Test
    @DisplayName("crear -> Debe lanzar EntityNotFoundException si el paciente no existe")
    void crear_PacienteNoExiste_LanzaException() {
        Mockito.when(centroRepo.findById(1L)).thenReturn(Optional.of(centroMock));
        Mockito.when(usuarioRepo.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.crear(requestValido));
        Mockito.verify(repository, Mockito.times(0)).save(any());
    }

    @Test
    @DisplayName("crear -> Debe lanzar IllegalStateException si el paciente ya está inscrito en ese centro")
    void crear_PacienteYaInscritoEnCentro_LanzaException() {
        Mockito.when(centroRepo.findById(1L)).thenReturn(Optional.of(centroMock));
        Mockito.when(usuarioRepo.findById(10L)).thenReturn(Optional.of(pacienteMock));
        
        // Simular que ya tiene una inscripción previa en el mismo centro (ID 1)
        List<ListaEsperaLocal> inscripcionesExistentes = List.of(listaEsperaMock);
        Mockito.when(repository.findByPacienteId(10L)).thenReturn(inscripcionesExistentes);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.crear(requestValido));
        
        assertEquals("El paciente ya se encuentra en la lista de espera de este centro médico.", exception.getMessage());
        Mockito.verify(repository, Mockito.times(0)).save(any());
    }

    @Test
    @DisplayName("crear -> Debe lanzar IllegalArgumentException si la prioridad es menor a 1")
    void crear_PrioridadInvalida_LanzaException() {
        requestValido.setPrioridad(0); // Prioridad inválida

        Mockito.when(centroRepo.findById(1L)).thenReturn(Optional.of(centroMock));
        Mockito.when(usuarioRepo.findById(10L)).thenReturn(Optional.of(pacienteMock));
        Mockito.when(repository.findByPacienteId(10L)).thenReturn(new ArrayList<>());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.crear(requestValido));

        assertEquals("La prioridad debe ser un valor válido mayor a 0.", exception.getMessage());
        Mockito.verify(repository, Mockito.times(0)).save(any());
    }

    // ==========================================
    // PRUEBAS: actualizar
    // ==========================================

    @Test
    @DisplayName("actualizar -> Debe modificar la prioridad correctamente")
    void actualizar_Valido_ModificaYGuarda() {
        ListaEsperaRequest reqUpdate = new ListaEsperaRequest();
        reqUpdate.setPrioridad(5);

        Mockito.when(repository.findById(100L)).thenReturn(Optional.of(listaEsperaMock));
        Mockito.when(repository.save(any(ListaEsperaLocal.class))).thenAnswer(inv -> inv.getArgument(0));

        ListaEsperaLocal resultado = service.actualizar(100L, reqUpdate);

        assertEquals(5, resultado.getPrioridad());
        Mockito.verify(repository, Mockito.times(1)).save(any());
    }

    @Test
    @DisplayName("actualizar -> Debe lanzar IllegalArgumentException si la nueva prioridad es menor a 1")
    void actualizar_PrioridadInvalida_LanzaException() {
        ListaEsperaRequest reqUpdate = new ListaEsperaRequest();
        reqUpdate.setPrioridad(-2);

        Mockito.when(repository.findById(100L)).thenReturn(Optional.of(listaEsperaMock));

        assertThrows(IllegalArgumentException.class, () -> service.actualizar(100L, reqUpdate));
        Mockito.verify(repository, Mockito.times(0)).save(any());
    }

    // ==========================================
    // PRUEBAS: eliminar
    // ==========================================

    @Test
    @DisplayName("eliminar -> Debe borrar si el ID existe")
    void eliminar_Existe_BorraCorrectamente() {
        Mockito.when(repository.existsById(100L)).thenReturn(true);
        Mockito.doNothing().when(repository).deleteById(100L);

        assertDoesNotThrow(() -> service.eliminar(100L));
        Mockito.verify(repository, Mockito.times(1)).deleteById(100L);
    }

    @Test
    @DisplayName("eliminar -> Debe lanzar EntityNotFoundException si el ID no existe")
    void eliminar_NoExiste_LanzaException() {
        Mockito.when(repository.existsById(999L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.eliminar(999L));
        Mockito.verify(repository, Mockito.times(0)).deleteById(any());
    }
}