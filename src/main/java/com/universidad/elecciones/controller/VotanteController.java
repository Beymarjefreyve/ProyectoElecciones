package com.universidad.elecciones.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.universidad.elecciones.dto.VotanteRequestDTO;
import com.universidad.elecciones.dto.VotanteResponseDTO;
import com.universidad.elecciones.entity.Votante;
import com.universidad.elecciones.service.VotanteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/votantes")
@RequiredArgsConstructor
public class VotanteController {

	@Autowired
    VotanteService service;

    @GetMapping
    public List<VotanteResponseDTO> listar() {
        return service.listar();
    }

    @PostMapping
    public VotanteResponseDTO registrar(@RequestBody VotanteRequestDTO dto) {
        return service.registrar(dto);
    }
}