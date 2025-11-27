package com.universidad.elecciones.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.universidad.elecciones.dto.TipoSolicitudRequestDTO;
import com.universidad.elecciones.dto.TipoSolicitudResponseDTO;
import com.universidad.elecciones.entity.TipoSolicitud;
import com.universidad.elecciones.repository.TipoSolicitudRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoSolicitudService {

	@Autowired
    private final TipoSolicitudRepository repo;

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
    // MAPPER ENTITY → DTO
    // =====================================================
    private TipoSolicitudResponseDTO buildDTO(TipoSolicitud ts) {
        return TipoSolicitudResponseDTO.builder()
                .id(ts.getId())
                .nombre(ts.getNombre())
                .build();
    }
}