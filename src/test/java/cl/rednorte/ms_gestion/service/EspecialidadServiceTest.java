package cl.rednorte.ms_gestion.service;

import cl.rednorte.ms_gestion.entity.Especialidad;
import cl.rednorte.ms_gestion.repository.EspecialidadRepository;
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
class EspecialidadServiceTest {

    @Mock
    private EspecialidadRepository repository;

    @InjectMocks
    private EspecialidadService service;

    private Especialidad especialidadBase;

    @BeforeEach
    void setUp() {
        especialidadBase = new Especialidad();
        especialidadBase.setId(1L);
        especialidadBase.setNombre("Pediatría");
    }

    // ==========================================
    // PRUEBAS PARA: obtenerPorId
    // ==========================================

    @Test
    @DisplayName("obtenerPorId -> Debe retornar la especialidad cuando existe")
    void obtenerPorId_Existe_RetornaEspecialidad() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(especialidadBase));

        Especialidad resultado = service.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Pediatría", resultado.getNombre());
    }

    @Test
    @DisplayName("obtenerPorId -> Debe lanzar EntityNotFoundException si no existe")
    void obtenerPorId_NoExiste_LanzaException() {
        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            service.obtenerPorId(99L);
        });

        assertEquals("Especialidad no encontrada con ID: 99", exception.getMessage());
    }

    // ==========================================
    // PRUEBAS PARA: crear
    // ==========================================

    @Test
    @DisplayName("crear -> Debe guardar exitosamente si el nombre es válido y no está duplicado")
    void crear_Valido_GuardaCorrectamente() {
        Mockito.when(repository.findByNombreIgnoreCase("Pediatría")).thenReturn(Optional.empty());
        Mockito.when(repository.save(any(Especialidad.class))).thenReturn(especialidadBase);

        Especialidad creada = service.crear(especialidadBase);

        assertNotNull(creada);
        Mockito.verify(repository, Mockito.times(1)).save(any(Especialidad.class));
    }

    @Test
    @DisplayName("crear -> Debe lanzar IllegalArgumentException si el nombre es vacío o nulo")
    void crear_NombreVacio_LanzaException() {
        especialidadBase.setNombre("   "); // Solo espacios en blanco

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.crear(especialidadBase);
        });

        assertEquals("El nombre de la especialidad es obligatorio.", exception.getMessage());
        Mockito.verify(repository, Mockito.times(0)).save(any(Especialidad.class));
    }

    @Test
    @DisplayName("crear -> Debe lanzar IllegalArgumentException si el nombre ya existe")
    void crear_NombreDuplicado_LanzaException() {
        Mockito.when(repository.findByNombreIgnoreCase("Pediatría")).thenReturn(Optional.of(especialidadBase));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.crear(especialidadBase);
        });

        assertTrue(exception.getMessage().contains("ya existe en el sistema"));
        Mockito.verify(repository, Mockito.times(0)).save(any(Especialidad.class));
    }

    // ==========================================
    // PRUEBAS PARA: actualizar
    // ==========================================

    @Test
    @DisplayName("actualizar -> Debe modificar el nombre si no pertenece a otra especialidad")
    void actualizar_NombreNuevoValido_ModificaYGuarda() {
        Especialidad request = new Especialidad();
        request.setNombre("Cardiología");

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(especialidadBase));
        Mockito.when(repository.findByNombreIgnoreCase("Cardiología")).thenReturn(Optional.empty());
        Mockito.when(repository.save(any(Especialidad.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Especialidad resultado = service.actualizar(1L, request);

        assertEquals("Cardiología", resultado.getNombre());
        Mockito.verify(repository, Mockito.times(1)).save(any(Especialidad.class));
    }

    @Test
    @DisplayName("actualizar -> Debe permitir guardar si el nombre coincide con la especialidad actual")
    void actualizar_MismoNombre_PermiteGuardar() {
        Especialidad request = new Especialidad();
        request.setNombre("Pediatría"); // El mismo nombre que ya tiene ID = 1

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(especialidadBase));
        Mockito.when(repository.findByNombreIgnoreCase("Pediatría")).thenReturn(Optional.of(especialidadBase));
        Mockito.when(repository.save(any(Especialidad.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Especialidad resultado = service.actualizar(1L, request);

        assertEquals("Pediatría", resultado.getNombre());
        Mockito.verify(repository, Mockito.times(1)).save(any(Especialidad.class));
    }

    @Test
    @DisplayName("actualizar -> Debe lanzar IllegalArgumentException si el nuevo nombre ya le pertenece a OTRA especialidad")
    void actualizar_NombreOcupadoPorOtro_LanzaException() {
        Especialidad otraEspecialidad = new Especialidad();
        otraEspecialidad.setId(2L); // ID diferente
        otraEspecialidad.setNombre("Traumatología");

        Especialidad request = new Especialidad();
        request.setNombre("Traumatología");

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(especialidadBase));
        Mockito.when(repository.findByNombreIgnoreCase("Traumatología")).thenReturn(Optional.of(otraEspecialidad));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.actualizar(1L, request);
        });

        assertTrue(exception.getMessage().contains("ya existe en el sistema"));
        Mockito.verify(repository, Mockito.times(0)).save(any(Especialidad.class));
    }

    @Test
    @DisplayName("actualizar -> Debe lanzar IllegalArgumentException si el payload viene con el nombre vacío")
    void actualizar_NombreVacio_LanzaException() {
        Especialidad request = new Especialidad();
        request.setNombre("");

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(especialidadBase));

        assertThrows(IllegalArgumentException.class, () -> {
            service.actualizar(1L, request);
        });
        Mockito.verify(repository, Mockito.times(0)).save(any(Especialidad.class));
    }

    // ==========================================
    // PRUEBAS PARA: eliminar
    // ==========================================

    @Test
    @DisplayName("eliminar -> Debe borrar la especialidad si el ID existe")
    void eliminar_Existe_EliminaCorrectamente() {
        Mockito.when(repository.existsById(1L)).thenReturn(true);
        Mockito.doNothing().when(repository).deleteById(1L);

        assertDoesNotThrow(() -> service.eliminar(1L));

        Mockito.verify(repository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar -> Debe lanzar EntityNotFoundException si el ID no existe")
    void eliminar_NoExiste_LanzaException() {
        Mockito.when(repository.existsById(99L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> {
            service.eliminar(99L);
        });

        Mockito.verify(repository, Mockito.times(0)).deleteById(99L);
    }
}