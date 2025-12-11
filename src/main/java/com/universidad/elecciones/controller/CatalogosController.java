package com.universidad.elecciones.controller;

import com.universidad.elecciones.dto.*;
import com.universidad.elecciones.service.*;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/catalogos")
@RequiredArgsConstructor
public class CatalogosController {
	@Autowired
   FacultadService facultadService;
	@Autowired
   ProgramaService programaService;
	@Autowired
   SedeService sedeService;
	@Autowired
   TipoEleccionService tipoEleccionService;
	@Autowired
   TipoService tipoService;

    // ===============================================
    // FACULTADES
    // ===============================================
    @GetMapping("/facultades")
    public List<FacultadDTO> listarFacultades() {
        return facultadService.listar();
    }

    @GetMapping("/facultades/{id}")
    public FacultadDTO buscarFacultadPorId(@PathVariable Long id) {
        return facultadService.buscarPorId(id);
    }

    @PostMapping("/facultades")
    public ResponseEntity<FacultadDTO> crearFacultad(@RequestBody FacultadRequestDTO dto) {
        FacultadDTO response = facultadService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/facultades/{id}")
    public FacultadDTO actualizarFacultad(@PathVariable Long id, @RequestBody FacultadRequestDTO dto) {
        return facultadService.actualizar(id, dto);
    }

    @DeleteMapping("/facultades/{id}")
    public ResponseEntity<Void> eliminarFacultad(@PathVariable Long id) {
        facultadService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ===============================================
    // PROGRAMAS
    // ===============================================
    @GetMapping("/programas")
    public List<ProgramaDTO> listarProgramas() {
        return programaService.listar();
    }

    @GetMapping("/programas/{id}")
    public ProgramaDTO buscarProgramaPorId(@PathVariable Long id) {
        return programaService.buscarPorId(id);
    }

    @PostMapping("/programas")
    public ResponseEntity<ProgramaDTO> crearPrograma(@RequestBody ProgramaRequestDTO dto) {
        ProgramaDTO response = programaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/programas/{id}")
    public ProgramaDTO actualizarPrograma(@PathVariable Long id, @RequestBody ProgramaRequestDTO dto) {
        return programaService.actualizar(id, dto);
    }

    @DeleteMapping("/programas/{id}")
    public ResponseEntity<Void> eliminarPrograma(@PathVariable Long id) {
        programaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ===============================================
    // SEDES
    // ===============================================
    @GetMapping("/sedes")
    public List<SedeResponseDTO> listarSedes() {
        return sedeService.listar();
    }

    @GetMapping("/sedes/{id}")
    public SedeResponseDTO buscarSedePorId(@PathVariable Long id) {
        return sedeService.buscarPorId(id);
    }

    @PostMapping("/sedes")
    public ResponseEntity<SedeResponseDTO> crearSede(@RequestBody SedeRequestDTO dto) {
        SedeResponseDTO response = sedeService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/sedes/{id}")
    public SedeResponseDTO actualizarSede(@PathVariable Long id, @RequestBody SedeRequestDTO dto) {
        return sedeService.actualizar(id, dto);
    }

    @DeleteMapping("/sedes/{id}")
    public ResponseEntity<Void> eliminarSede(@PathVariable Long id) {
        sedeService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ===============================================
    // TIPOS
    // ===============================================
    @GetMapping("/tipos")
    public List<TipoResponseDTO> listarTipos() {
        return tipoService.listar();
    }

    @GetMapping("/tipos/{id}")
    public TipoResponseDTO buscarTipoPorId(@PathVariable Long id) {
        return tipoService.buscarPorId(id);
    }

    @PostMapping("/tipos")
    public ResponseEntity<TipoResponseDTO> crearTipo(@RequestBody TipoRequestDTO dto) {
        TipoResponseDTO response = tipoService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/tipos/{id}")
    public TipoResponseDTO actualizarTipo(@PathVariable Long id, @RequestBody TipoRequestDTO dto) {
        return tipoService.actualizar(id, dto);
    }

    @DeleteMapping("/tipos/{id}")
    public ResponseEntity<Void> eliminarTipo(@PathVariable Long id) {
        tipoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ===============================================
    // TIPOS DE ELECCIÓN
    // ===============================================
    @GetMapping("/tipos-eleccion")
    public List<TipoEleccionResponseDTO> listarTiposEleccion() {
        return tipoEleccionService.listar();
    }

    @GetMapping("/tipos-eleccion/{id}")
    public TipoEleccionResponseDTO buscarTipoEleccionPorId(@PathVariable Long id) {
        return tipoEleccionService.buscarPorId(id);
    }

    @PostMapping("/tipos-eleccion")
    public ResponseEntity<TipoEleccionResponseDTO> crearTipoEleccion(@RequestBody TipoEleccionRequestDTO dto) {
        TipoEleccionResponseDTO response = tipoEleccionService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/tipos-eleccion/{id}")
    public TipoEleccionResponseDTO actualizarTipoEleccion(@PathVariable Long id, @RequestBody TipoEleccionRequestDTO dto) {
        return tipoEleccionService.actualizar(id, dto);
    }

    @DeleteMapping("/tipos-eleccion/{id}")
    public ResponseEntity<Void> eliminarTipoEleccion(@PathVariable Long id) {
        tipoEleccionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    

  

 
}