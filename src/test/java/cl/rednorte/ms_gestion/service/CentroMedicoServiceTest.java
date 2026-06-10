package cl.rednorte.ms_gestion.service;

import cl.rednorte.ms_gestion.entity.CentroMedico;
import cl.rednorte.ms_gestion.repository.CentroMedicoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CentroMedicoServiceTest {

    @Mock
    private CentroMedicoRepository repository;

    @InjectMocks
    private CentroMedicoService service;

    private CentroMedico centroBase;

    @BeforeEach
    void setUp() {
        centroBase = new CentroMedico();
        centroBase.setId(1L);
        centroBase.setNombreSucursal("RedNorte Pudahuel");
        centroBase.setRegion("Metropolitana");
        centroBase.setComuna("Pudahuel");
        centroBase.setDireccion("Av. San Pablo 1234");
    }

    @Test
    @DisplayName("obtenerPorId -> Debe retornar el centro cuando existe")
    void obtenerPorId_Existe_RetornaCentro() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(centroBase));

        CentroMedico resultado = service.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("RedNorte Pudahuel", resultado.getNombreSucursal());
    }

    @Test
    @DisplayName("obtenerPorId -> Debe lanzar EntityNotFoundException si no existe")
    void obtenerPorId_NoExiste_LanzaException() {
        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            service.obtenerPorId(99L);
        });

        assertEquals("Centro médico no encontrado con ID: 99", exception.getMessage());
    }


    @Test
    @DisplayName("crearCentro -> Debe guardar el centro si tiene datos válidos")
    void crearCentro_Valido_GuardaCorrectamente() {
        Mockito.when(repository.save(any(CentroMedico.class))).thenReturn(centroBase);

        CentroMedico creado = service.crearCentro(centroBase);

        assertNotNull(creado);
        verifySaveCalled(1);
    }

    @Test
    @DisplayName("crearCentro -> Debe lanzar IllegalArgumentException si el nombre es vacío o nulo")
    void crearCentro_NombreInvalido_LanzaException() {
        centroBase.setNombreSucursal(""); // Nombre vacío

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.crearCentro(centroBase);
        });

        assertEquals("El nombre de la sucursal es obligatorio.", exception.getMessage());
        verifySaveCalled(0); // Asegura que nunca tocó la base de datos
    }

    @Test
    @DisplayName("actualizarCentro -> Debe modificar todos los campos si el payload es válido")
    void actualizarCentro_Valido_ModificaYGuarda() {
        CentroMedico request = new CentroMedico();
        request.setNombreSucursal("RedNorte Maipú");
        request.setRegion("Metropolitana");
        request.setComuna("Maipú");
        request.setDireccion("Nueva Dirección 555");

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(centroBase));
        Mockito.when(repository.save(any(CentroMedico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CentroMedico actualizado = service.actualizarCentro(1L, request);

        assertEquals("RedNorte Maipú", actualizado.getNombreSucursal());
        assertEquals("Maipú", actualizado.getComuna());
        assertEquals("Nueva Dirección 555", actualizado.getDireccion());
    }

    @Test
    @DisplayName("actualizarCentro -> Debe lanzar IllegalArgumentException si el nuevo nombre viene vacío")
    void actualizarCentro_NombreVacio_LanzaException() {
        CentroMedico request = new CentroMedico();
        request.setNombreSucursal("   "); // Solo espacios

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(centroBase));

        assertThrows(IllegalArgumentException.class, () -> {
            service.actualizarCentro(1L, request);
        });
        verifySaveCalled(0);
    }

    // ==========================================
    // PRUEBAS PARA: parchearCentro
    // ==========================================

    @Test
    @DisplayName("parchearCentro -> Debe aplicar parches parciales dinámicamente")
    void parchearCentro_Parcial_ActualizaSoloCamposEnviados() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("comuna", "Pudahuel Sur");
        updates.put("direccion", "Calle Modificada 777");

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(centroBase));
        Mockito.when(repository.save(any(CentroMedico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CentroMedico resultado = service.parchearCentro(1L, updates);

        assertEquals("RedNorte Pudahuel", resultado.getNombreSucursal()); // Mantiene el original
        assertEquals("Pudahuel Sur", resultado.getComuna());             // Cambió
        assertEquals("Calle Modificada 777", resultado.getDireccion());   // Cambió
    }

    @Test
    @DisplayName("parchearCentro -> Debe fallar al intentar parchar el nombre por un valor vacío")
    void parchearCentro_NombreInvalido_LanzaException() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("nombreSucursal", "");

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(centroBase));

        assertThrows(IllegalArgumentException.class, () -> {
            service.parchearCentro(1L, updates);
        });
        verifySaveCalled(0);
    }

    @Test
    @DisplayName("eliminarCentro -> Debe borrar si el ID existe en la base de datos")
    void eliminarCentro_Existe_EliminaExitosamente() {
        Mockito.when(repository.existsById(1L)).thenReturn(true);
        Mockito.doNothing().when(repository).deleteById(1L);

        assertDoesNotThrow(() -> service.eliminarCentro(1L));

        Mockito.verify(repository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("eliminarCentro -> Debe lanzar EntityNotFoundException si el ID no existe")
    void eliminarCentro_NoExiste_LanzaException() {
        Mockito.when(repository.existsById(99L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> {
            service.eliminarCentro(99L);
        });

        Mockito.verify(repository, Mockito.times(0)).deleteById(99L);
    }

    
    private void verifySaveCalled(int times) {
        Mockito.verify(repository, Mockito.times(times)).save(any(CentroMedico.class));
    }
}