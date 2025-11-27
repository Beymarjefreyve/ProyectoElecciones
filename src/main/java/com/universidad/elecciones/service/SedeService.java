package com.universidad.elecciones.service;

import com.universidad.elecciones.dto.SedeResponseDTO;
import com.universidad.elecciones.repository.SedeRepository;
import com.universidad.elecciones.entity.Sede;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SedeService {

	@Autowired
    private final SedeRepository repo;

    public List<SedeResponseDTO> listar() {
        return repo.findAll()
                .stream()
                .map(s -> SedeResponseDTO.builder()
                        .id(s.getId())
                        .nombre(s.getNombre())
                        .build())
                .collect(Collectors.toList());
    }
}