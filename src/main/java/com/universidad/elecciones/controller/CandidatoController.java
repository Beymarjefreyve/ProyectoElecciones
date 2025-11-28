package com.universidad.elecciones.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.universidad.elecciones.dto.CandidatoRequestDTO;
import com.universidad.elecciones.dto.CandidatoResponseDTO;
import com.universidad.elecciones.service.CandidatoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/candidatos")
@RequiredArgsConstructor
public class CandidatoController {

	@Autowired
    CandidatoService service;

    // ===============================================
    // GET - LISTAR TODOS LOS CANDIDATOS
    // ===============================================
    @GetMapping
    public List<CandidatoResponseDTO> listar() {
        return service.listar();
    }

    // ===============================================
    // GET - BUSCAR CANDIDATO POR ID
    // ===============================================
    @GetMapping("/{id}")
    public CandidatoResponseDTO buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    // ===============================================
    // GET - BUSCAR CANDIDATO POR DOCUMENTO
    // ===============================================
    @GetMapping("/documento/{documento}")
    public CandidatoResponseDTO buscarPorDocumento(@PathVariable String documento) {
        return service.buscarPorDocumento(documento);
    }

    // ===============================================
    // POST - CREAR CANDIDATO
    // ===============================================
    @PostMapping
    public ResponseEntity<CandidatoResponseDTO> crear(@RequestBody CandidatoRequestDTO dto) {
        CandidatoResponseDTO response = service.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ===============================================
    // PUT - ACTUALIZAR CANDIDATO
    // ===============================================
    @PutMapping("/{id}")
    public CandidatoResponseDTO actualizar(
            @PathVariable Long id,
            @RequestBody CandidatoRequestDTO dto) {
        return service.actualizar(id, dto);
    }

    // ===============================================
    // DELETE - ELIMINAR CANDIDATO
    // ===============================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
