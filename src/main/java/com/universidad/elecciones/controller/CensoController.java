package com.universidad.elecciones.controller;

import com.universidad.elecciones.dto.CensoCargaMasivaRequest;
import com.universidad.elecciones.dto.CensoCargaMasivaResponse;
import com.universidad.elecciones.dto.CensoEliminacionMasivaRequest;
import com.universidad.elecciones.dto.CensoRequest;
import com.universidad.elecciones.dto.CensoResponse;
import com.universidad.elecciones.service.CensoService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    // GET - BUSCAR POR VOTANTE Y ELECCIÓN
    // ==========================================================
    @GetMapping("/censo/buscar")
    public CensoResponse buscarPorVotanteYEleccion(
            @RequestParam Long votanteId,
            @RequestParam Long eleccionId) {
        return service.buscarPorVotanteYEleccion(votanteId, eleccionId);
    }

    // ==========================================================
    // GET - VALIDAR SI UN VOTANTE ESTÁ EN EL CENSO
    // ==========================================================
    @GetMapping("/censo/validar")
    public ResponseEntity<Map<String, Object>> validarSiEstaEnCenso(
            @RequestParam Long votanteId,
            @RequestParam Long eleccionId) {
        boolean estaEnCenso = service.validarSiEstaEnCenso(votanteId, eleccionId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("votanteId", votanteId);
        response.put("eleccionId", eleccionId);
        response.put("estaEnCenso", estaEnCenso);
        response.put("mensaje", estaEnCenso 
            ? "El votante está registrado en el censo de esta elección" 
            : "El votante NO está registrado en el censo de esta elección");
        
        return ResponseEntity.ok(response);
    }

    // ==========================================================
    // POST - AGREGAR VOTANTE AL CENSO DE UNA ELECCIÓN
    // ==========================================================
    @PostMapping("/api/censo")
    public ResponseEntity<CensoResponse> agregar(@RequestBody CensoRequest request) {
        CensoResponse response = service.agregar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==========================================================
    // POST - CARGA MASIVA DE VOTANTES
    // ==========================================================
    @PostMapping("/api/censo/carga-masiva")
    public ResponseEntity<CensoCargaMasivaResponse> cargaMasiva(@RequestBody CensoCargaMasivaRequest request) {
        CensoCargaMasivaResponse response = service.cargaMasiva(request);
        return ResponseEntity.ok(response);
    }

    // ==========================================================
    // DELETE - ELIMINAR DEL CENSO
    // ==========================================================
    @DeleteMapping("/api/censo")
    public ResponseEntity<Void> eliminar(
            @RequestParam Long votanteId,
            @RequestParam Long eleccionId) {
        service.eliminar(votanteId, eleccionId);
        return ResponseEntity.noContent().build();
    }

    // ==========================================================
    // DELETE - ELIMINAR MÚLTIPLES VOTANTES DEL CENSO
    // ==========================================================
    @DeleteMapping("/api/censo/eliminacion-masiva")
    public ResponseEntity<CensoCargaMasivaResponse> eliminarMultiples(@RequestBody CensoEliminacionMasivaRequest request) {
        CensoCargaMasivaResponse response = service.eliminarMultiples(request);
        return ResponseEntity.ok(response);
    }
}