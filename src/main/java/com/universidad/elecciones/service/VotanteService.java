package com.universidad.elecciones.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.universidad.elecciones.dto.VotanteRequestDTO;
import com.universidad.elecciones.dto.VotanteResponseDTO;
import com.universidad.elecciones.entity.Votante;
import com.universidad.elecciones.repository.VotanteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VotanteService {

	@Autowired
    private final VotanteRepository repo;

    // ===============================================
    // REGISTRAR UN VOTANTE
    // ===============================================
    public VotanteResponseDTO registrar(VotanteRequestDTO dto) {

        if (repo.existsByDocumento(dto.getDocumento())) {
            throw new RuntimeException("Este documento ya existe");
        }

        Votante v = new Votante();
        v.setDocumento(dto.getDocumento());
        v.setNombre(dto.getNombre());

        Votante saved = repo.save(v);

        return buildDTO(saved);
    }

    // ===============================================
    // LISTAR
    // ===============================================
    public List<VotanteResponseDTO> listar() {
        return repo.findAll()
                .stream()
                .map(this::buildDTO)
                .collect(Collectors.toList());
    }

    // ===============================================
    // Mapper ENTITY → DTO
    // ===============================================
    private VotanteResponseDTO buildDTO(Votante v) {
        return VotanteResponseDTO.builder()
                .id(v.getId())
                .documento(v.getDocumento())
                .nombre(v.getNombre())
                .build();
    }
}