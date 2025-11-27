package com.universidad.elecciones.controller;

import com.universidad.elecciones.dto.TipoRequestDTO;
import com.universidad.elecciones.dto.TipoResponseDTO;
import com.universidad.elecciones.service.TipoService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipos")
@RequiredArgsConstructor
public class TipoController {

	@Autowired
    TipoService service;

    @GetMapping
    public List<TipoResponseDTO> listar() {
        return service.listar();
    }

    @PostMapping
    public TipoResponseDTO crear(@RequestBody TipoRequestDTO dto) {
        return service.crear(dto);
    }
}