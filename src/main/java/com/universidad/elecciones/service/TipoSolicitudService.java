package com.universidad.elecciones.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.universidad.elecciones.dto.TipoSolicitudRequestDTO;
import com.universidad.elecciones.dto.TipoSolicitudResponseDTO;
import com.universidad.elecciones.entity.TipoSolicitud;
import com.universidad.elecciones.repository.SolicitudRepository;
import com.universidad.elecciones.repository.TipoSolicitudRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoSolicitudService {

	@Autowired
    private final TipoSolicitudRepository repo;
	@Autowired
    private final SolicitudRepository solicitudRepo;

    // =====================================================
    // LISTAR
    // =====================================================
    public List<TipoSolicitudResponseDTO> listar() {
        return repo.findAll()
                .stream()
                .map(this::buildDTO)
                .collect(Collectors.toList());
    }

    // =====================================================
    // CREAR
    // =====================================================
    public TipoSolicitudResponseDTO crear(TipoSolicitudRequestDTO dto) {
        TipoSolicitud ts = new TipoSolicitud();
        ts.setNombre(dto.getNombre());

        TipoSolicitud saved = repo.save(ts);
        return buildDTO(saved);
    }

    // =====================================================
    // ACTUALIZAR
    // =====================================================
    public TipoSolicitudResponseDTO actualizar(Long id, TipoSolicitudRequestDTO dto) {
        TipoSolicitud tipoSolicitud = buscarPorIdEntity(id);
        tipoSolicitud.setNombre(dto.getNombre());
        
        TipoSolicitud updated = repo.save(tipoSolicitud);
        return buildDTO(updated);
    }

    // =====================================================
    // ELIMINAR
    // =====================================================
    public void eliminar(Long id) {
        TipoSolicitud tipoSolicitud = buscarPorIdEntity(id);
        
        // Validar integridad referencial: verificar si está en solicitudes
        if (!solicitudRepo.findByTipoSolicitudId(id).isEmpty()) {
            throw new RuntimeException("No se puede eliminar el tipo de solicitud porque está asociado a solicitudes");
        }
        
        repo.delete(tipoSolicitud);
    }

    // =====================================================
    // BUSCAR POR ID
    // =====================================================
    public TipoSolicitudResponseDTO buscarPorId(Long id) {
        TipoSolicitud tipoSolicitud = buscarPorIdEntity(id);
        return buildDTO(tipoSolicitud);
    }

    // =====================================================
    // MÉTODOS PRIVADOS AUXILIARES
    // =====================================================
    
    private TipoSolicitud buscarPorIdEntity(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de solicitud no encontrado"));
    }

    // =====================================================
    // MAPPER ENTITY → DTO
    // =====================================================
    private TipoSolicitudResponseDTO buildDTO(TipoSolicitud ts) {
        return TipoSolicitudResponseDTO.builder()
                .id(ts.getId())
                .nombre(ts.getNombre())
                .build();
    }
}