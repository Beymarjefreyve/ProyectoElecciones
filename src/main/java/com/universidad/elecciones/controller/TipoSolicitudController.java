package com.universidad.elecciones.controller;

import com.universidad.elecciones.dto.TipoSolicitudRequestDTO;
import com.universidad.elecciones.dto.TipoSolicitudResponseDTO;
import com.universidad.elecciones.service.TipoSolicitudService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipo-solicitud")
@RequiredArgsConstructor
public class TipoSolicitudController {

	@Autowired
    TipoSolicitudService service;

    @GetMapping
    public List<TipoSolicitudResponseDTO> listar() {
        return service.listar();
    }

    @PostMapping
    public TipoSolicitudResponseDTO crear(@RequestBody TipoSolicitudRequestDTO dto) {
        return service.crear(dto);
    }
}