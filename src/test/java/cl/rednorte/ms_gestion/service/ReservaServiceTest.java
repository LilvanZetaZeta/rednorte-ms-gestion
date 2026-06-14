package cl.rednorte.ms_gestion.service;

import cl.rednorte.ms_gestion.client.CupoLiberadoClient;
import cl.rednorte.ms_gestion.client.NotificacionClient;
import cl.rednorte.ms_gestion.dto.BloqueoAgendaRequest;
import cl.rednorte.ms_gestion.dto.ReservaRequest;
import cl.rednorte.ms_gestion.entity.CentroMedico;
import cl.rednorte.ms_gestion.entity.Reserva;
import cl.rednorte.ms_gestion.entity.Usuario;
import cl.rednorte.ms_gestion.repository.CentroMedicoRepository;
import cl.rednorte.ms_gestion.repository.ReservaRepository;
import cl.rednorte.ms_gestion.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock private ReservaRepository reservaRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private CentroMedicoRepository centroMedicoRepository;
    @Mock private NotificacionClient notificacionClient;
    @Mock private CupoLiberadoClient cupoLiberadoClient;

    @InjectMocks private ReservaService service;

    private Usuario pacienteMock;
    private Usuario medicoMock;
    private Usuario secretariaMock;
    private CentroMedico centroMock;
    private ReservaRequest requestBase;
    private Reserva reservaMock;

    @BeforeEach
    void setUp() {
        // 1. Instanciar Entidades de Soporte
        centroMock = new CentroMedico();
        centroMock.setId(1L);
        centroMock.setNombreSucursal("RedNorte Pudahuel");

        pacienteMock = new Usuario();
        pacienteMock.setId(10L);
        pacienteMock.setCorreo("ivan@example.com");
        pacienteMock.setNombreCompleto("Iván Castro");
        pacienteMock.setRol(Usuario.RolUsuario.PACIENTE);

        medicoMock = new Usuario();
        medicoMock.setId(20L);
        medicoMock.setNombreCompleto("Dr. Casa");
        medicoMock.setRol(Usuario.RolUsuario.MEDICO);

        secretariaMock = new Usuario();
        secretariaMock.setId(30L);
        secretariaMock.setRol(Usuario.RolUsuario.SECRETARIA);
        secretariaMock.setCentroMedico(centroMock); // Secretaria asociada al centro 1

        // 2. Instanciar Request y Entidad de Prueba
        requestBase = new ReservaRequest();
        requestBase.setCentroId(1L);
        requestBase.setMedicoId(20L);
        requestBase.setFechaHora(LocalDateTime.now().plusDays(2)); // Cita en 48 horas
        requestBase.setOrigen(Reserva.OrigenReserva.WEB);
        requestBase.setTipoReserva(Reserva.TipoReserva.CONSULTA_MEDICA);

        reservaMock = new Reserva();
        reservaMock.setId(500L);
        reservaMock.setPaciente(pacienteMock);
        reservaMock.setMedico(medicoMock);
        reservaMock.setCentro(centroMock);
        reservaMock.setFechaHora(requestBase.getFechaHora());
        reservaMock.setEstado(Reserva.EstadoReserva.VIGENTE);
        reservaMock.setTipoReserva(Reserva.TipoReserva.CONSULTA_MEDICA);
    }

    @AfterEach
    void tearDown() {
        // Limpiar el contexto de seguridad para evitar fugas de memoria entre tests
        SecurityContextHolder.clearContext();
    }

    // Helper estratégico para inyectar un usuario simulado en el SecurityContext de Spring
    private void mockSecurityUser(String username) {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn(username);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    // ==========================================
    // SECCIÓN DE TESTS: método crear()
    // ==========================================

    @Test
    @DisplayName("crear -> Flujo Paciente: Reserva propia exitosa con disparo de notificación")
    void crear_PacienteReservaPropia_Exito() {
        mockSecurityUser("auth-paciente-123");
        
        Mockito.when(usuarioRepository.findByIdAuth("auth-paciente-123")).thenReturn(Optional.of(pacienteMock));
        Mockito.when(usuarioRepository.findById(20L)).thenReturn(Optional.of(medicoMock));
        Mockito.when(centroMedicoRepository.findById(1L)).thenReturn(Optional.of(centroMock));
        Mockito.when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaMock);

        Reserva resultado = service.crear(requestBase);

        assertNotNull(resultado);
        assertEquals(Reserva.EstadoReserva.VIGENTE, resultado.getEstado());
        Mockito.verify(notificacionClient, Mockito.times(1)).notificarReserva(any());
        Mockito.verify(reservaRepository, Mockito.times(1)).save(any());
    }

    @Test
    @DisplayName("crear -> Flujo Secretaria: Lanza excepción si intenta agendar en un centro que no es el suyo")
    void crear_SecretariaCentroAjeno_LanzaException() {
        mockSecurityUser("auth-secre-123");
        requestBase.setCentroId(2L); // Petición para el centro 2, pero ella es del centro 1

        Mockito.when(usuarioRepository.findByIdAuth("auth-secre-123")).thenReturn(Optional.of(secretariaMock));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.crear(requestBase));
        
        assertTrue(exception.getMessage().contains("No puedes crear reservas para una sucursal distinta"));
        Mockito.verify(reservaRepository, Mockito.times(0)).save(any());
    }

    @Test
    @DisplayName("crear -> Flujo Admin: Registra paciente nuevo de forma automática si no existe por RUT")
    void crear_AdminPacienteNuevoPorRut_RegistraYAgenda() {
        mockSecurityUser("auth-admin-123");
        
        Usuario adminMock = new Usuario();
        adminMock.setRol(Usuario.RolUsuario.ADMINISTRATIVO);

        requestBase.setPacienteRut("20111222-3");
        requestBase.setPacienteCorreo("nuevo@paciente.cl");
        requestBase.setPacienteNombreCompleto("Juan Pérez");

        Mockito.when(usuarioRepository.findByIdAuth("auth-admin-123")).thenReturn(Optional.of(adminMock));
        Mockito.when(usuarioRepository.findByRut("20111222-3")).thenReturn(Optional.empty()); // No existe
        Mockito.when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0)); // Simula registro exitoso
        Mockito.when(usuarioRepository.findById(any())).thenReturn(Optional.of(medicoMock));
        Mockito.when(centroMedicoRepository.findById(any())).thenReturn(Optional.of(centroMock));
        Mockito.when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaMock);

        Reserva resultado = service.crear(requestBase);

        assertNotNull(resultado);
        // Verifica que se haya guardado el nuevo usuario paciente en el repositorio
        Mockito.verify(usuarioRepository, Mockito.times(1)).save(any(Usuario.class));
    }

    // ==========================================
    // SECCIÓN DE TESTS: método cancelar()
    // ==========================================

    @Test
    @DisplayName("cancelar -> Flujo Paciente: Lanza excepción si intenta cancelar con menos de 24 horas de anticipación")
    void cancelar_PacienteMenos24Horas_LanzaException() {
        mockSecurityUser("auth-paciente-123");
        reservaMock.setFechaHora(LocalDateTime.now().plusHours(5)); // Cita en 5 horas

        Mockito.when(reservaRepository.findById(500L)).thenReturn(Optional.of(reservaMock));
        Mockito.when(usuarioRepository.findByIdAuth("auth-paciente-123")).thenReturn(Optional.of(pacienteMock));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.cancelar(500L));

        assertEquals("No puede cancelar su cita con menos de 24 horas de anticipación.", exception.getMessage());
        Mockito.verify(reservaRepository, Mockito.times(0)).save(any());
    }

    @Test
    @DisplayName("cancelar -> Flujo Secretaria: Pasa a estado PENDIENTE_CANCELACION_ADMIN si faltan menos de 24 horas")
    void cancelar_SecretariaMenos24Horas_CambiaAPendienteAdmin() {
        mockSecurityUser("auth-secre-123");
        reservaMock.setFechaHora(LocalDateTime.now().plusHours(5)); // Cita en 5 horas

        Mockito.when(reservaRepository.findById(500L)).thenReturn(Optional.of(reservaMock));
        Mockito.when(usuarioRepository.findByIdAuth("auth-secre-123")).thenReturn(Optional.of(secretariaMock));
        Mockito.when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        Reserva resultado = service.cancelar(500L);

        assertEquals(Reserva.EstadoReserva.PENDIENTE_CANCELACION_ADMIN, resultado.getEstado());
        Mockito.verify(cupoLiberadoClient, Mockito.times(0)).notificarCupoLiberado(any()); // No libera cupo todavía
    }

    @Test
    @DisplayName("cancelar -> Flujo Normal: Cancela con éxito y gatilla evento de cupo liberado")
    void cancelar_TiempoSuficiente_CancelaYNotificaCupo() {
        mockSecurityUser("auth-paciente-123");
        reservaMock.setFechaHora(LocalDateTime.now().plusHours(30)); // Cita en 30 horas (más de un día)

        Mockito.when(reservaRepository.findById(500L)).thenReturn(Optional.of(reservaMock));
        Mockito.when(usuarioRepository.findByIdAuth("auth-paciente-123")).thenReturn(Optional.of(pacienteMock));
        Mockito.when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        Reserva resultado = service.cancelar(500L);

        assertEquals(Reserva.EstadoReserva.CANCELADA, resultado.getEstado());
        // Comprobar que gatilla el flujo de CupoLiberadoClient
        Mockito.verify(cupoLiberadoClient, Mockito.times(1)).notificarCupoLiberado(any());
    }

    // ==========================================
    // SECCIÓN DE TESTS: método bloquearAgendaYProcesarCitas()
    // ==========================================

    @Test
    @DisplayName("bloquearAgenda -> Cambia a CANCELADA todas las citas del médico en el día bloqueado")
    void bloquearAgenda_ConCitasActivas_CancelaTodasLasCitas() {
        BloqueoAgendaRequest reqBloqueo = new BloqueoAgendaRequest();
        reqBloqueo.setMedicoId(20L);
        reqBloqueo.setFechaBloqueo(LocalDate.now().plusDays(5));

        List<Reserva> citasDelDia = new ArrayList<>(List.of(reservaMock)); // Una cita vigente

        Mockito.when(reservaRepository.findByMedicoIdAndFechaHoraBetweenAndEstadoIn(
                Mockito.eq(20L), any(), any(), any()
        )).thenReturn(citasDelDia);

        service.bloquearAgendaYProcesarCitas(reqBloqueo);

        assertEquals(Reserva.EstadoReserva.CANCELADA, reservaMock.getEstado());
        Mockito.verify(reservaRepository, Mockito.times(1)).saveAll(citasDelDia);
    }
}