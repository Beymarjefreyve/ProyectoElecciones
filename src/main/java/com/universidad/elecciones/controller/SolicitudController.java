package com.universidad.elecciones.controller;

import com.universidad.elecciones.dto.SolicitudRequestDTO;
import com.universidad.elecciones.dto.SolicitudResponseDTO;
import com.universidad.elecciones.service.SolicitudService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

	@Autowired
    SolicitudService service;

    // ===============================================
    // POST - CREAR SOLICITUD
    // ===============================================
    @PostMapping
    public ResponseEntity<SolicitudResponseDTO> crear(@RequestBody SolicitudRequestDTO dto) {
        SolicitudResponseDTO response = service.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ===============================================
    // GET - LISTAR TODAS LAS SOLICITUDES
    // ===============================================
    @GetMapping
    public List<SolicitudResponseDTO> listar() {
        return service.listar();
    }

    // ===============================================
    // GET - BUSCAR SOLICITUD POR ID
    // ===============================================
    @GetMapping("/{id}")
    public SolicitudResponseDTO buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    // ===============================================
    // GET - FILTRAR POR ESTADO
    // ===============================================
    @GetMapping("/filtro/estado")
    public List<SolicitudResponseDTO> filtrarPorEstado(@RequestParam String estado) {
        return service.filtrarPorEstado(estado);
    }

    // ===============================================
    // GET - FILTRAR POR TIPO DE SOLICITUD
    // ===============================================
    @GetMapping("/filtro/tipo-solicitud")
    public List<SolicitudResponseDTO> filtrarPorTipoSolicitud(@RequestParam Long tipoSolicitudId) {
        return service.filtrarPorTipoSolicitud(tipoSolicitudId);
    }

    // ===============================================
    // GET - FILTRAR POR PROGRAMA
    // ===============================================
    @GetMapping("/filtro/programa")
    public List<SolicitudResponseDTO> filtrarPorPrograma(@RequestParam Long programaId) {
        return service.filtrarPorPrograma(programaId);
    }

    // ===============================================
    // GET - FILTRAR POR SEDE
    // ===============================================
    @GetMapping("/filtro/sede")
    public List<SolicitudResponseDTO> filtrarPorSede(@RequestParam Long sedeId) {
        return service.filtrarPorSede(sedeId);
    }

    // ===============================================
    // GET - FILTRAR POR PROGRAMA Y SEDE
    // ===============================================
    @GetMapping("/filtro/programa-sede")
    public List<SolicitudResponseDTO> filtrarPorProgramaYSede(
            @RequestParam Long programaId,
            @RequestParam Long sedeId) {
        return service.filtrarPorProgramaYSede(programaId, sedeId);
    }

    // ===============================================
    // PATCH - ACTUALIZAR ESTADO DE SOLICITUD
    // ===============================================
    @PatchMapping("/{id}/estado")
    public SolicitudResponseDTO actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        return service.actualizarEstado(id, estado);
    }

    // ===============================================
    // PATCH - APROBAR SOLICITUD
    // ===============================================
    @PatchMapping("/{id}/aprobar")
    public SolicitudResponseDTO aprobar(@PathVariable Long id) {
        return service.aprobar(id);
    }

    // ===============================================
    // PATCH - RECHAZAR SOLICITUD
    // ===============================================
    @PatchMapping("/{id}/rechazar")
    public SolicitudResponseDTO rechazar(@PathVariable Long id) {
        return service.rechazar(id);
    }
}