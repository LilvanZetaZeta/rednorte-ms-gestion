package cl.rednorte.ms_gestion.service;

import cl.rednorte.ms_gestion.entity.CentroMedico;
import cl.rednorte.ms_gestion.entity.Usuario;
import cl.rednorte.ms_gestion.entity.Reserva;
import cl.rednorte.ms_gestion.entity.ListaEsperaLocal;
import cl.rednorte.ms_gestion.repository.CentroMedicoRepository;
import cl.rednorte.ms_gestion.repository.UsuarioRepository;
import cl.rednorte.ms_gestion.repository.ReservaRepository;
import cl.rednorte.ms_gestion.repository.ListaEsperaLocalRepository;
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
import java.util.List;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
class CentroMedicoServiceTest {

    @Mock
    private CentroMedicoRepository repository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private ListaEsperaLocalRepository listaEsperaLocalRepository;

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
    @DisplayName("crearCentro -> Debe lanzar IllegalArgumentException si el nombre de la sucursal ya existe")
    void crearCentro_NombreDuplicado_LanzaException() {
        Mockito.when(repository.findByNombreSucursalIgnoreCase("RedNorte Pudahuel")).thenReturn(Optional.of(centroBase));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.crearCentro(centroBase);
        });

        assertEquals("El centro médico 'RedNorte Pudahuel' ya existe en el sistema.", exception.getMessage());
        verifySaveCalled(0);
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

    @Test
    @DisplayName("actualizarCentro -> Debe lanzar IllegalArgumentException si el nuevo nombre ya está en uso por otra sucursal")
    void actualizarCentro_NombreDuplicado_LanzaException() {
        CentroMedico request = new CentroMedico();
        request.setNombreSucursal("RedNorte Maipú");

        CentroMedico otroCentro = new CentroMedico();
        otroCentro.setId(2L);
        otroCentro.setNombreSucursal("RedNorte Maipú");

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(centroBase));
        Mockito.when(repository.findByNombreSucursalIgnoreCase("RedNorte Maipú")).thenReturn(Optional.of(otroCentro));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.actualizarCentro(1L, request);
        });

        assertEquals("El centro médico 'RedNorte Maipú' ya existe en el sistema.", exception.getMessage());
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
    @DisplayName("parchearCentro -> Debe lanzar IllegalArgumentException si el nuevo nombre ya está en uso por otra sucursal")
    void parchearCentro_NombreDuplicado_LanzaException() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("nombreSucursal", "RedNorte Maipú");

        CentroMedico otroCentro = new CentroMedico();
        otroCentro.setId(2L);
        otroCentro.setNombreSucursal("RedNorte Maipú");

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(centroBase));
        Mockito.when(repository.findByNombreSucursalIgnoreCase("RedNorte Maipú")).thenReturn(Optional.of(otroCentro));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.parchearCentro(1L, updates);
        });

        assertEquals("El centro médico 'RedNorte Maipú' ya existe en el sistema.", exception.getMessage());
        verifySaveCalled(0);
    }

    @Test
    @DisplayName("eliminarCentro -> Debe borrar si el ID existe en la base de datos y realizar la cascada y desasociación")
    void eliminarCentro_Existe_EliminaExitosamente() {
        Mockito.when(repository.existsById(1L)).thenReturn(true);

        Usuario mockUsuario = new Usuario();
        mockUsuario.setId(10L);
        mockUsuario.setCentroMedico(centroBase);
        List<Usuario> mockUsuarios = new ArrayList<>();
        mockUsuarios.add(mockUsuario);

        Reserva mockReserva = new Reserva();
        mockReserva.setId(20L);
        mockReserva.setCentro(centroBase);
        List<Reserva> mockReservas = new ArrayList<>();
        mockReservas.add(mockReserva);

        ListaEsperaLocal mockEspera = new ListaEsperaLocal();
        mockEspera.setId(30L);
        mockEspera.setCentro(centroBase);
        List<ListaEsperaLocal> mockEsperas = new ArrayList<>();
        mockEsperas.add(mockEspera);

        Mockito.when(usuarioRepository.findByCentroMedicoId(1L)).thenReturn(mockUsuarios);
        Mockito.when(reservaRepository.findByCentroId(1L)).thenReturn(mockReservas);
        Mockito.when(listaEsperaLocalRepository.findByCentroIdOrderByPrioridadAsc(1L)).thenReturn(mockEsperas);

        assertDoesNotThrow(() -> service.eliminarCentro(1L));

        assertNull(mockUsuario.getCentroMedico());
        Mockito.verify(usuarioRepository, Mockito.times(1)).saveAll(mockUsuarios);
        Mockito.verify(reservaRepository, Mockito.times(1)).deleteAll(mockReservas);
        Mockito.verify(listaEsperaLocalRepository, Mockito.times(1)).deleteAll(mockEsperas);
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