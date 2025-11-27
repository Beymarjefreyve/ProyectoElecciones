package com.universidad.elecciones.service;

import com.universidad.elecciones.dto.FacultadDTO;
import com.universidad.elecciones.entity.Facultad;
import com.universidad.elecciones.repository.FacultadRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacultadService {

	@Autowired
    private final FacultadRepository repo;

    public List<FacultadDTO> listar() {
        return repo.findAll()
                .stream()
                .map(f -> FacultadDTO.builder()
                        .id(f.getId())
                        .nombre(f.getNombre())
                        .build())
                .collect(Collectors.toList());
    }
}