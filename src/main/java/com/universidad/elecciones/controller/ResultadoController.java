package com.universidad.elecciones.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.universidad.elecciones.dto.ConteoResultadosDTO;
import com.universidad.elecciones.dto.ParticipacionDTO;
import com.universidad.elecciones.dto.VotoRequest;
import com.universidad.elecciones.dto.VotoResponse;
import com.universidad.elecciones.service.ResultadoService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/elecciones")
@RequiredArgsConstructor
public class ResultadoController {

	@Autowired
    ResultadoService service;

    // ---------------------------------------------------------
    // POST /elecciones/{id}/votar
    // ---------------------------------------------------------
    @PostMapping("/{id}/votar")
    public ResponseEntity<VotoResponse> votar(
            @PathVariable Long id,
            @RequestBody VotoRequest req,
            HttpServletRequest http) {

        String ip = http.getRemoteAddr();

        return ResponseEntity.ok(service.votar(id, req, ip));
    }

    // ---------------------------------------------------------
    // GET /elecciones/{id}/votos
    // ---------------------------------------------------------
    @GetMapping("/{id}/votos")
    public ResponseEntity<List<VotoResponse>> listarVotos(@PathVariable Long id) {

        return ResponseEntity.ok(service.listarVotos(id));
    }
    
    
    
    @GetMapping("/{id}/resultados")
    public List<ConteoResultadosDTO> conteo(@PathVariable Long id) {
        return service.conteoPorEleccion(id);
    }

    @GetMapping("/{id}/participacion")
    public ParticipacionDTO participacion(@PathVariable Long id) {
        return service.calcularParticipacion(id);
    }
    
    
    
    
    
}