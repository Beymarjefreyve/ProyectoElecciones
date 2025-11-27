package com.universidad.elecciones.controller;

import com.universidad.elecciones.dto.InscripcionRequest;
import com.universidad.elecciones.dto.InscripcionResponse;
import com.universidad.elecciones.service.InscripcionService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/elecciones")
@RequiredArgsConstructor
public class EleccionInscripcionController {

	@Autowired
    InscripcionService service;

    // POST /elecciones/{id}/inscripciones
    @PostMapping("/{id}/inscripciones")
    public ResponseEntity<InscripcionResponse> inscribir(
            @PathVariable Long id,
            @RequestBody InscripcionRequest request) {

        return ResponseEntity.ok(service.inscribir(id, request));
    }

    // GET /elecciones/{id}/candidatos
    @GetMapping("/{id}/candidatos")
    public ResponseEntity<List<InscripcionResponse>> listarCandidatos(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.listarCandidatos(id));
    }
}