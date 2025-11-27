package com.universidad.elecciones.service;

import com.universidad.elecciones.dto.ProgramaDTO;
import com.universidad.elecciones.entity.Programa;
import com.universidad.elecciones.repository.ProgramaRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramaService {

	@Autowired
    private final ProgramaRepository repo;

    public List<ProgramaDTO> listar() {
        return repo.findAll()
                .stream()
                .map(p -> ProgramaDTO.builder()
                        .id(p.getId())
                        .nombre(p.getNombre())
                        .facultadId(p.getFacultad().getId())
                        .build())
                .collect(Collectors.toList());
    }
}