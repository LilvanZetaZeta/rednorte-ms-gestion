package cl.rednorte.ms_registro.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.rednorte.ms_registro.client.NotificacionesClient;
import cl.rednorte.ms_registro.dto.ReservaRequestDTO;
import cl.rednorte.ms_registro.dto.ReservaResponseDTO;
import cl.rednorte.ms_registro.entity.CentroMedico;
import cl.rednorte.ms_registro.entity.Reserva;
import cl.rednorte.ms_registro.entity.Usuario;
import cl.rednorte.ms_registro.enums.EstadoReservaEnum;
import cl.rednorte.ms_registro.repository.CentroMedicoRepository;
import cl.rednorte.ms_registro.repository.ReservaRepository;
import cl.rednorte.ms_registro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CentroMedicoRepository centroMedicoRepository;
    
    // NUEVO: Inyectamos el cliente que se comunicará con el MS-Notificaciones
    private final NotificacionesClient notificacionesClient;

    private ReservaResponseDTO mapearAResponse(Reserva reserva) {
        ReservaResponseDTO dto = new ReservaResponseDTO();
        dto.setId(reserva.getId());
        dto.setPacienteNombre(reserva.getPaciente().getNombreCompleto());
        dto.setCentroNombre(reserva.getCentroMedico().getNombreSucursal());
        dto.setFechaHora(reserva.getFechaHora());
        dto.setEstado(reserva.getEstado());
        return dto;
    }

    public List<ReservaResponseDTO> listarTodas() {
        return reservaRepository.findAll().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    public List<ReservaResponseDTO> listarPorPaciente(UUID pacienteId) {
        return reservaRepository.findByPacienteId(pacienteId).stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservaResponseDTO crearReserva(ReservaRequestDTO dto) {
        Usuario paciente = usuarioRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new IllegalArgumentException("Error: El paciente con ID " + dto.getPacienteId() + " no existe."));

        CentroMedico centro = centroMedicoRepository.findById(dto.getCentroId())
                .orElseThrow(() -> new IllegalArgumentException("Error: El centro médico con ID " + dto.getCentroId() + " no existe."));

        Reserva reserva = new Reserva();
        reserva.setPaciente(paciente);
        reserva.setCentroMedico(centro);
        reserva.setFechaHora(dto.getFechaHora());
        reserva.setOrigen(dto.getOrigen());

        Reserva reservaGuardada = reservaRepository.save(reserva);

        // NUEVO: Enviar el correo automáticamente
        try {
            Map<String, Object> payloadCorreo = new HashMap<>();
            payloadCorreo.put("templateId", "template_zfrdshb"); // Reemplaza por tu ID real si es distinto
            payloadCorreo.put("toEmail", paciente.getCorreo());
            
            Map<String, String> params = new HashMap<>();
            params.put("user_name", paciente.getNombreCompleto());
            params.put("order_number", reservaGuardada.getId().toString());
            params.put("date", reservaGuardada.getFechaHora().toString());
            
            payloadCorreo.put("params", params);
            
            notificacionesClient.enviarCorreo(payloadCorreo);
        } catch (Exception e) {
            System.err.println("La reserva se creó, pero falló el envío del correo: " + e.getMessage());
        }

        return mapearAResponse(reservaGuardada);
    }

    public ReservaResponseDTO obtenerPorId(UUID id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada con ID: " + id));
        return mapearAResponse(reserva);
    }

    @Transactional
    public ReservaResponseDTO actualizarReserva(UUID id, ReservaRequestDTO dto, EstadoReservaEnum nuevoEstado) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada con ID: " + id));

        if (reserva.getEstado() == EstadoReservaEnum.CANCELADA && nuevoEstado == EstadoReservaEnum.CONFIRMADA) {
            throw new IllegalStateException("No se puede confirmar una reserva que ya ha sido cancelada.");
        }

        reserva.setFechaHora(dto.getFechaHora());
        reserva.setEstado(nuevoEstado);
        reserva.setOrigen(dto.getOrigen());

        return mapearAResponse(reservaRepository.save(reserva));
    }
}