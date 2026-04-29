package cl.rednorte.ms_registro.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // --- TRADUCTOR: De Entidad a DTO ---
    private ReservaResponseDTO mapearAResponse(Reserva reserva) {
        ReservaResponseDTO dto = new ReservaResponseDTO();
        dto.setId(reserva.getId());
        dto.setPacienteNombre(reserva.getPaciente().getNombreCompleto());
        dto.setCentroNombre(reserva.getCentroMedico().getNombreSucursal());
        dto.setFechaHora(reserva.getFechaHora());
        dto.setEstado(reserva.getEstado());
        return dto;
    }

    // --- LISTAR ---
    public List<ReservaResponseDTO> listarTodas() {
        return reservaRepository.findAll().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    // --- LISTAR POR PACIENTE ---
    public List<ReservaResponseDTO> listarPorPaciente(UUID pacienteId) {
        return reservaRepository.findByPacienteId(pacienteId).stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }
    

    // --- CREAR (CON VALIDACIÓN DE RELACIONES) ---
    @Transactional
    public ReservaResponseDTO crearReserva(ReservaRequestDTO dto) {
        // 1. Validar que el paciente existe
        Usuario paciente = usuarioRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new IllegalArgumentException("Error: El paciente con ID " + dto.getPacienteId() + " no existe."));

        // 2. Validar que el centro médico existe
        CentroMedico centro = centroMedicoRepository.findById(dto.getCentroId())
                .orElseThrow(() -> new IllegalArgumentException("Error: El centro médico con ID " + dto.getCentroId() + " no existe."));

        // 3. Crear la entidad y asignar las relaciones reales
        Reserva reserva = new Reserva();
        reserva.setPaciente(paciente);
        reserva.setCentroMedico(centro);
        reserva.setFechaHora(dto.getFechaHora());
        reserva.setOrigen(dto.getOrigen());

        // 4. Guardar y retornar el DTO
        return mapearAResponse(reservaRepository.save(reserva));
    }

    // --- OBTENER POR ID ---
    public ReservaResponseDTO obtenerPorId(UUID id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada con ID: " + id));
        return mapearAResponse(reserva);
    }

    // --- ACTUALIZAR (Estado, Fecha u Origen) ---
    @Transactional
    public ReservaResponseDTO actualizarReserva(UUID id, ReservaRequestDTO dto, EstadoReservaEnum nuevoEstado) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada con ID: " + id));

        // Regla de negocio: No se puede confirmar una reserva cancelada
        if (reserva.getEstado() == EstadoReservaEnum.CANCELADA && nuevoEstado == EstadoReservaEnum.CONFIRMADA) {
            throw new IllegalStateException("No se puede confirmar una reserva que ya ha sido cancelada.");
        }

        reserva.setFechaHora(dto.getFechaHora());
        reserva.setEstado(nuevoEstado);
        reserva.setOrigen(dto.getOrigen());

        return mapearAResponse(reservaRepository.save(reserva));
    }
}