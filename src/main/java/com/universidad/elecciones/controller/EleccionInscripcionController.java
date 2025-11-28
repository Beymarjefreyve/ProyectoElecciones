package com.universidad.elecciones.controller;

import com.universidad.elecciones.dto.InscripcionRequest;
import com.universidad.elecciones.dto.InscripcionResponse;
import com.universidad.elecciones.dto.InscripcionUpdateRequest;
import com.universidad.elecciones.service.InscripcionService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/elecciones")
@RequiredArgsConstructor
public class EleccionInscripcionController {

	@Autowired
    InscripcionService service;

    // ===============================================
    // POST - INSCRIBIR CANDIDATO EN ELECCIÓN
    // ===============================================
    @PostMapping("/{id}/inscripciones")
    public ResponseEntity<InscripcionResponse> inscribir(
            @PathVariable Long id,
            @RequestBody InscripcionRequest request) {
        InscripcionResponse response = service.inscribir(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ===============================================
    // GET - LISTAR CANDIDATOS DE UNA ELECCIÓN
    // ===============================================
    @GetMapping("/{id}/candidatos")
    public ResponseEntity<List<InscripcionResponse>> listarCandidatos(
            @PathVariable Long id) {
        return ResponseEntity.ok(service.listarCandidatos(id));
    }

    // ===============================================
    // GET - BUSCAR INSCRIPCIÓN POR ID
    // ===============================================
    @GetMapping("/inscripciones/{inscripcionId}")
    public ResponseEntity<InscripcionResponse> buscarPorId(
            @PathVariable Long inscripcionId) {
        return ResponseEntity.ok(service.buscarPorId(inscripcionId));
    }

    // ===============================================
    // PUT - ACTUALIZAR INSCRIPCIÓN
    // ===============================================
    @PutMapping("/inscripciones/{inscripcionId}")
    public ResponseEntity<InscripcionResponse> actualizar(
            @PathVariable Long inscripcionId,
            @RequestBody InscripcionUpdateRequest request) {
        return ResponseEntity.ok(service.actualizar(inscripcionId, request));
    }

    // ===============================================
    // DELETE - ELIMINAR INSCRIPCIÓN
    // ===============================================
    @DeleteMapping("/inscripciones/{inscripcionId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long inscripcionId) {
        service.eliminar(inscripcionId);
        return ResponseEntity.noContent().build();
    }

    // ===============================================
    // PATCH - CAMBIAR ESTADO DE INSCRIPCIÓN
    // ===============================================
    @PatchMapping("/inscripciones/{inscripcionId}/estado")
    public ResponseEntity<InscripcionResponse> cambiarEstado(
            @PathVariable Long inscripcionId,
            @RequestParam String estado) {
        return ResponseEntity.ok(service.cambiarEstado(inscripcionId, estado));
    }
}