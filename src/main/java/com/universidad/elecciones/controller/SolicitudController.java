package com.universidad.elecciones.controller;

import com.universidad.elecciones.dto.SolicitudRequestDTO;
import com.universidad.elecciones.dto.SolicitudResponseDTO;
import com.universidad.elecciones.service.SolicitudService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

	@Autowired
    SolicitudService service;

    @PostMapping
    public SolicitudResponseDTO crear(@RequestBody SolicitudRequestDTO dto) {
        return service.crear(dto);
    }

    @GetMapping
    public List<SolicitudResponseDTO> listar() {
        return service.listar();
    }
}