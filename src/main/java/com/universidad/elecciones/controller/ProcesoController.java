package com.universidad.elecciones.controller;

import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.universidad.elecciones.dto.ProcesoRequestDTO;
import com.universidad.elecciones.dto.ProcesoResponseDTO;
import com.universidad.elecciones.service.ProcesoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/procesos")
@RequiredArgsConstructor
public class ProcesoController {

	@Autowired
   ProcesoService service;

    // ===============================================
    // GET - LISTAR TODOS LOS PROCESOS
    // ===============================================
    @GetMapping
    public List<ProcesoResponseDTO> listar() {
        return service.listar();
    }

    // ===============================================
    // GET - LISTAR PROCESOS ACTIVOS
    // ===============================================
    @GetMapping("/activos")
    public List<ProcesoResponseDTO> listarActivos() {
        return service.listarActivos();
    }

    // ===============================================
    // GET - BUSCAR PROCESO POR ID
    // ===============================================
    @GetMapping("/{id}")
    public ProcesoResponseDTO buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    // ===============================================
    // GET - FILTRAR POR RANGO DE FECHAS
    // ===============================================
    @GetMapping("/filtro/rango-fechas")
    public List<ProcesoResponseDTO> filtrarPorRangoFechas(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin) {
        return service.filtrarPorRangoFechas(fechaInicio, fechaFin);
    }

    // ===============================================
    // POST - CREAR PROCESO
    // ===============================================
    @PostMapping
    public ResponseEntity<ProcesoResponseDTO> crear(@RequestBody ProcesoRequestDTO dto) {
        ProcesoResponseDTO response = service.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ===============================================
    // PUT - ACTUALIZAR PROCESO
    // ===============================================
    @PutMapping("/{id}")
    public ProcesoResponseDTO actualizar(
            @PathVariable Long id,
            @RequestBody ProcesoRequestDTO dto) {
        return service.actualizar(id, dto);
    }

    // ===============================================
    // DELETE - ELIMINAR PROCESO
    // ===============================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
