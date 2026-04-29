package cl.rednorte.ms_registro.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.rednorte.ms_registro.dto.ListaEsperaRequestDTO;
import cl.rednorte.ms_registro.dto.ListaEsperaResponseDTO;
import cl.rednorte.ms_registro.entity.CentroMedico;
import cl.rednorte.ms_registro.entity.ListaEspera;
import cl.rednorte.ms_registro.entity.Usuario;
import cl.rednorte.ms_registro.enums.EstadoListaEsperaEnum;
import cl.rednorte.ms_registro.repository.CentroMedicoRepository;
import cl.rednorte.ms_registro.repository.ListaEsperaRepository;
import cl.rednorte.ms_registro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListaEsperaService {

    private final ListaEsperaRepository listaEsperaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CentroMedicoRepository centroMedicoRepository;

    // --- TRADUCTOR ---
    private ListaEsperaResponseDTO mapearAResponse(ListaEspera fila) {
        ListaEsperaResponseDTO dto = new ListaEsperaResponseDTO();
        dto.setId(fila.getId());
        dto.setPacienteNombre(fila.getPaciente().getNombreCompleto());
        dto.setCentroNombre(fila.getCentroMedico().getNombreSucursal());
        dto.setFechaIngreso(fila.getFechaIngreso());
        dto.setEstado(fila.getEstado());
        return dto;
    }

    // --- 1. INGRESAR A LA FILA ---
    @Transactional
    public ListaEsperaResponseDTO ingresarALaFila(ListaEsperaRequestDTO dto) {
        // Validar existencia de entidades
        Usuario paciente = usuarioRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new IllegalArgumentException("Error: Paciente no existe."));

        CentroMedico centro = centroMedicoRepository.findById(dto.getCentroId())
                .orElseThrow(() -> new IllegalArgumentException("Error: Centro médico no existe."));

        // 3. Regla de Negocio: Evitar duplicados en la misma fila
        if (listaEsperaRepository.existsByPacienteIdAndCentroMedicoIdAndEstado(dto.getPacienteId(), dto.getCentroId(), EstadoListaEsperaEnum.EN_ESPERA)) {
            throw new IllegalStateException("El paciente ya se encuentra en la lista de espera de esta sucursal.");
        }

        ListaEspera fila = new ListaEspera();
        fila.setPaciente(paciente);
        fila.setCentroMedico(centro);
        
        // El servidor asigna la hora exacta e inviolable
        fila.setFechaIngreso(LocalDateTime.now());
        fila.setEstado(EstadoListaEsperaEnum.EN_ESPERA);
        

        return mapearAResponse(listaEsperaRepository.save(fila));
    }

    // --- 2. VER LA FILA ORDENADA (Por Centro Médico) ---
    public List<ListaEsperaResponseDTO> obtenerFilaPorCentro(UUID centroId) {
        // Solo trae a los que están EN_ESPERA, ignorando cancelados o ya asignados
        return listaEsperaRepository.findByCentroMedicoIdAndEstadoOrderByFechaIngresoAsc(centroId, EstadoListaEsperaEnum.EN_ESPERA)
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    // --- 3. ACTUALIZAR ESTADO (Avanzar en la fila) ---
    @Transactional
    public ListaEsperaResponseDTO actualizarEstado(UUID id, EstadoListaEsperaEnum nuevoEstado) {
        ListaEspera fila = listaEsperaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro de lista de espera no encontrado con ID: " + id));

        fila.setEstado(nuevoEstado);
        return mapearAResponse(listaEsperaRepository.save(fila));
    }
}