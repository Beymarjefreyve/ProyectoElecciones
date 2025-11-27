package com.universidad.elecciones.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.universidad.elecciones.dto.TipoRequestDTO;
import com.universidad.elecciones.dto.TipoResponseDTO;
import com.universidad.elecciones.entity.Tipo;
import com.universidad.elecciones.repository.TipoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoService {

	@Autowired
    private final TipoRepository tipoRepository;

    // ===============================================
    // LISTAR
    // ===============================================
    public List<TipoResponseDTO> listar() {
        return tipoRepository.findAll()
                .stream()
                .map(this::buildDTO)
                .collect(Collectors.toList());
    }

    // ===============================================
    // CREAR
    // ===============================================
    public TipoResponseDTO crear(TipoRequestDTO dto) {

        Tipo tipo = new Tipo();
        tipo.setNombre(dto.getNombre());

        Tipo saved = tipoRepository.save(tipo);

        return buildDTO(saved);
    }

    // ===============================================
    // MAPPER ENTITY → DTO
    // ===============================================
    private TipoResponseDTO buildDTO(Tipo tipo) {
        return TipoResponseDTO.builder()
                .id(tipo.getId())
                .nombre(tipo.getNombre())
                .build();
    }
}