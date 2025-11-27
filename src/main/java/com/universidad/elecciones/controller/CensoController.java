package com.universidad.elecciones.controller;

import com.universidad.elecciones.dto.CensoRequest;
import com.universidad.elecciones.dto.CensoResponse;
import com.universidad.elecciones.service.CensoService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class CensoController {

	@Autowired
	CensoService service;


    // ==========================================================
    // GET - LISTAR CENSO DE UNA ELECCIÓN
    // ==========================================================
    @GetMapping("/elecciones/{eleccionId}/censo")
    public List<CensoResponse> listarPorEleccion(@PathVariable Long eleccionId) {
        return service.listarPorEleccion(eleccionId);
    }

    // ==========================================================
    // POST - AGREGAR VOTANTE AL CENSO DE UNA ELECCIÓN
    // ==========================================================
    @PostMapping("/api/censo")
    public ResponseEntity<CensoResponse> agregar(@RequestBody CensoRequest request) {
        return ResponseEntity.ok(service.agregar(request));
    }
}