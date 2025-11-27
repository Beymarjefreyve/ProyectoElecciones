package com.universidad.elecciones.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.universidad.elecciones.dto.*;
import com.universidad.elecciones.service.ResultadoService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/elecciones")
@RequiredArgsConstructor
public class ResultadoController {

	@Autowired
    ResultadoService service;

    // ===============================================
    // POST - VOTAR
    // ===============================================
    @PostMapping("/{id}/votar")
    public ResponseEntity<VotoResponse> votar(
            @PathVariable Long id,
            @RequestBody VotoRequest req,
            HttpServletRequest http) {

        String ip = http.getRemoteAddr();
        return ResponseEntity.ok(service.votar(id, req, ip));
    }

    // ===============================================
    // GET - LISTAR VOTOS DE UNA ELECCIÓN
    // ===============================================
    @GetMapping("/{id}/votos")
    public ResponseEntity<List<VotoResponse>> listarVotos(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarVotos(id));
    }

    // ===============================================
    // GET - CONTEO DE RESULTADOS
    // ===============================================
    @GetMapping("/{id}/resultados")
    public ResponseEntity<List<ConteoResultadosDTO>> conteo(@PathVariable Long id) {
        return ResponseEntity.ok(service.conteoPorEleccion(id));
    }

    // ===============================================
    // GET - PARTICIPACIÓN
    // ===============================================
    @GetMapping("/{id}/participacion")
    public ResponseEntity<ParticipacionDTO> participacion(@PathVariable Long id) {
        return ResponseEntity.ok(service.calcularParticipacion(id));
    }

    // ===============================================
    // GET - ESTADÍSTICAS DETALLADAS
    // ===============================================
    @GetMapping("/{id}/estadisticas-detalladas")
    public ResponseEntity<EstadisticasDetalladasDTO> estadisticasDetalladas(@PathVariable Long id) {
        return ResponseEntity.ok(service.estadisticasDetalladas(id));
    }

    // ===============================================
    // GET - EXPORTAR RESULTADOS (CSV)
    // ===============================================
    @GetMapping("/{id}/exportar-csv")
    public ResponseEntity<String> exportarResultadosCSV(@PathVariable Long id) {
        String csv = service.exportarResultadosCSV(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "resultados_eleccion_" + id + ".csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csv);
    }

    // ===============================================
    // GET - HISTORIAL DE VOTOS POR VOTANTE
    // ===============================================
    @GetMapping("/votos/historial")
    public ResponseEntity<List<VotoResponse>> historialVotosPorVotante(
            @RequestParam String documento) {
        return ResponseEntity.ok(service.historialVotosPorVotante(documento));
    }

    // ===============================================
    // DELETE - ANULAR VOTO
    // ===============================================
    @DeleteMapping("/{id}/anular-voto")
    public ResponseEntity<Void> anularVoto(
            @PathVariable Long id,
            @RequestParam String documento) {
        service.anularVoto(id, documento);
        return ResponseEntity.noContent().build();
    }
}
