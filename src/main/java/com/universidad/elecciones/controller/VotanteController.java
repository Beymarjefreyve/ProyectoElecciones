package com.universidad.elecciones.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.universidad.elecciones.dto.VotanteRequestDTO;
import com.universidad.elecciones.dto.VotanteResponseDTO;
import com.universidad.elecciones.service.VotanteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/votantes")
@RequiredArgsConstructor
public class VotanteController {

    @Autowired
    VotanteService service;

    // ===============================================
    // GET - LISTAR TODOS LOS VOTANTES
    // ===============================================
    @GetMapping
    public List<VotanteResponseDTO> listar() {
        return service.listar();
    }

    // ===============================================
    // GET - BUSCAR VOTANTE POR ID
    // ===============================================
    @GetMapping("/{id}")
    public VotanteResponseDTO buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    // ===============================================
    // GET - BUSCAR VOTANTE POR DOCUMENTO
    // ===============================================
    @GetMapping("/documento/{documento}")
    public VotanteResponseDTO buscarPorDocumento(@PathVariable String documento) {
        return service.buscarPorDocumento(documento);
    }

    // ===============================================
    // POST - REGISTRAR VOTANTE
    // ===============================================
    @PostMapping
    public ResponseEntity<VotanteResponseDTO> registrar(@RequestBody VotanteRequestDTO dto) {
        VotanteResponseDTO response = service.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ===============================================
    // PUT - ACTUALIZAR VOTANTE
    // ===============================================
    @PutMapping("/{id}")
    public VotanteResponseDTO actualizar(
            @PathVariable Long id,
            @RequestBody VotanteRequestDTO dto) {
        return service.actualizar(id, dto);
    }

    // ===============================================
    // DELETE - ELIMINAR VOTANTE
    // ===============================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ===============================================
    // GET - VALIDAR SI PUEDE VOTAR EN UNA ELECCIÓN
    // ===============================================
    @GetMapping("/validar-voto")
    public ResponseEntity<Map<String, Object>> validarSiPuedeVotar(
            @RequestParam String documento,
            @RequestParam Long eleccionId) {
        Map<String, Object> response = service.validarSiPuedeVotar(documento, eleccionId);
        response.put("documento", documento);
        response.put("eleccionId", eleccionId);
        return ResponseEntity.ok(response);
    }
}