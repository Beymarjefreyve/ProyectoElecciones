package com.universidad.elecciones.service;

import com.universidad.elecciones.dto.TipoEleccionResponseDTO;
import com.universidad.elecciones.repository.TipoEleccionRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipoEleccionService {

	@Autowired
    private final TipoEleccionRepository repo;

    public List<TipoEleccionResponseDTO> listar() {
        return repo.findAll()
                .stream()
                .map(t -> TipoEleccionResponseDTO.builder()
                        .id(t.getId())
                        .nombre(t.getNombre())
                        .build())
                .collect(Collectors.toList());
    }
}