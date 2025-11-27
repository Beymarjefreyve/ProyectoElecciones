package com.universidad.elecciones.controller;

import com.universidad.elecciones.dto.EleccionRequestDTO;
import com.universidad.elecciones.dto.EleccionResponseDTO;
import com.universidad.elecciones.service.EleccionService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/elecciones")
@RequiredArgsConstructor
public class EleccionController {

	@Autowired
    EleccionService service;

    @GetMapping
    public List<EleccionResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public EleccionResponseDTO obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public EleccionResponseDTO crear(@RequestBody EleccionRequestDTO dto) {
        return service.crear(dto);
    }

    @PatchMapping("/{id}/estado")
    public EleccionResponseDTO cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado
    ) {
        return service.cambiarEstado(id, estado);
    }
}