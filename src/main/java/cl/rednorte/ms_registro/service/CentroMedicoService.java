package cl.rednorte.ms_registro.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import cl.rednorte.ms_registro.dto.CentroMedicoRequestDTO;
import cl.rednorte.ms_registro.dto.CentroMedicoResponseDTO;
import cl.rednorte.ms_registro.entity.CentroMedico;
import cl.rednorte.ms_registro.repository.CentroMedicoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CentroMedicoService {

    private final CentroMedicoRepository centroMedicoRepository;

    private CentroMedicoResponseDTO mapearAResponse(CentroMedico centro) {
        CentroMedicoResponseDTO dto = new CentroMedicoResponseDTO();
        dto.setId(centro.getId());
        dto.setNombreSucursal(centro.getNombreSucursal());
        return dto;
    }

    // --- LEER TODOS ---
    public List<CentroMedicoResponseDTO> listarTodos() {
        return centroMedicoRepository.findAll().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    // --- LEER POR ID (NUEVO) ---
    public CentroMedicoResponseDTO obtenerPorId(UUID id) {
        CentroMedico centro = centroMedicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Centro médico no encontrado con ID: " + id));
        return mapearAResponse(centro);
    }

    // --- CREAR ---
    public CentroMedicoResponseDTO registrarCentro(CentroMedicoRequestDTO dto) {
        if (centroMedicoRepository.existsByNombreSucursal(dto.getNombreSucursal())) {
            throw new IllegalArgumentException("Ya existe una sucursal con el nombre: " + dto.getNombreSucursal());
        }
        CentroMedico centro = new CentroMedico();
        centro.setNombreSucursal(dto.getNombreSucursal());
        return mapearAResponse(centroMedicoRepository.save(centro));
    }

    // --- ACTUALIZAR (NUEVO) ---
    public CentroMedicoResponseDTO actualizarCentro(UUID id, CentroMedicoRequestDTO dto) {
        CentroMedico centro = centroMedicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Centro médico no encontrado con ID: " + id));

        // Validar que el nuevo nombre no le pertenezca ya a otra sucursal
        if (!centro.getNombreSucursal().equals(dto.getNombreSucursal()) &&
            centroMedicoRepository.existsByNombreSucursal(dto.getNombreSucursal())) {
            throw new IllegalArgumentException("Ya existe otra sucursal con el nombre: " + dto.getNombreSucursal());
        }

        centro.setNombreSucursal(dto.getNombreSucursal());
        return mapearAResponse(centroMedicoRepository.save(centro));
    }

    // --- ELIMINAR ---
    public void eliminarCentro(UUID id) {
        if (!centroMedicoRepository.existsById(id)) {
            throw new IllegalArgumentException("Centro médico no encontrado con ID: " + id);
        }
        centroMedicoRepository.deleteById(id);
    }
}