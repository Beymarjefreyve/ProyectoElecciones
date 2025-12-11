package com.universidad.elecciones.controller;

import com.universidad.elecciones.dto.EleccionRequestDTO;
import com.universidad.elecciones.dto.EleccionResponseDTO;
import com.universidad.elecciones.service.EleccionService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/elecciones")
@RequiredArgsConstructor
public class EleccionController {

    @Autowired
    EleccionService service;

    // ===============================================
    // GET - LISTAR TODAS LAS ELECCIONES
    // ===============================================
    @GetMapping
    public List<EleccionResponseDTO> listar() {
        return service.listar();
    }

    // ===============================================
    // GET - LISTAR ELECCIONES ACTIVAS
    // ===============================================
    @GetMapping("/activas")
    public List<EleccionResponseDTO> listarActivas() {
        return service.listarActivas();
    }

    // ===============================================
    // GET - LISTAR ELECCIONES ABIERTAS
    // ===============================================
    @GetMapping("/abiertas")
    public List<EleccionResponseDTO> listarAbiertas() {
        return service.listarAbiertas();
    }

    // ===============================================
    // GET - LISTAR ELECCIONES ABIERTAS PARA UN VOTANTE
    // Filtra por censo, facultad y programa del votante
    // ===============================================
    @GetMapping("/abiertas/votante")
    public List<EleccionResponseDTO> listarAbiertasParaVotante(@RequestParam String documento) {
        return service.listarAbiertasParaVotante(documento);
    }

    // ===============================================
    // GET - BUSCAR ELECCIÓN POR ID
    // ===============================================
    @GetMapping("/{id}")
    public EleccionResponseDTO obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    // ===============================================
    // GET - FILTRAR POR ESTADO
    // ===============================================
    @GetMapping("/filtro/estado")
    public List<EleccionResponseDTO> filtrarPorEstado(@RequestParam String estado) {
        return service.filtrarPorEstado(estado);
    }

    // ===============================================
    // GET - FILTRAR POR PROCESO
    // ===============================================
    @GetMapping("/filtro/proceso")
    public List<EleccionResponseDTO> filtrarPorProceso(@RequestParam Long procesoId) {
        return service.filtrarPorProceso(procesoId);
    }

    // ===============================================
    // GET - FILTRAR POR TIPO DE ELECCIÓN
    // ===============================================
    @GetMapping("/filtro/tipo-eleccion")
    public List<EleccionResponseDTO> filtrarPorTipoEleccion(@RequestParam Long tipoEleccionId) {
        return service.filtrarPorTipoEleccion(tipoEleccionId);
    }

    // ===============================================
    // POST - CREAR ELECCIÓN
    // ===============================================
    @PostMapping
    public ResponseEntity<EleccionResponseDTO> crear(@RequestBody EleccionRequestDTO dto) {
        EleccionResponseDTO response = service.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ===============================================
    // PUT - ACTUALIZAR ELECCIÓN
    // ===============================================
    @PutMapping("/{id}")
    public EleccionResponseDTO actualizar(
            @PathVariable Long id,
            @RequestBody EleccionRequestDTO dto) {
        return service.actualizar(id, dto);
    }

    // ===============================================
    // DELETE - ELIMINAR/DESACTIVAR ELECCIÓN
    // ===============================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ===============================================
    // PATCH - CAMBIAR ESTADO
    // ===============================================
    @PatchMapping("/{id}/estado")
    public EleccionResponseDTO cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        return service.cambiarEstado(id, estado);
    }

    // ===============================================
    // PATCH - EXTENDER FECHA DE ELECCIÓN
    // ===============================================
    @PatchMapping("/{id}/extender-fecha")
    public EleccionResponseDTO extenderFecha(
            @PathVariable Long id,
            @RequestParam LocalDateTime nuevaFechaFinaliza) {
        return service.extenderFecha(id, nuevaFechaFinaliza);
    }

    // ===============================================
    // GET - VALIDAR QUE LA ELECCIÓN ESTÉ ABIERTA
    // ===============================================
    @GetMapping("/{id}/validar-abierta")
    public ResponseEntity<String> validarEleccionAbierta(@PathVariable Long id) {
        service.validarEleccionAbierta(id);
        return ResponseEntity.ok("La elección está abierta y disponible para votar");
    }
}