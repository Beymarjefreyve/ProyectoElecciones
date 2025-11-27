package com.universidad.elecciones.controller;

import com.universidad.elecciones.dto.*;
import com.universidad.elecciones.service.*;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/facultades")
    public List<FacultadDTO> facultades() {
        return facultadService.listar();
    }

    @GetMapping("/programas")
    public List<ProgramaDTO> programas() {
        return programaService.listar();
    }

    @GetMapping("/sedes")
    public List<SedeResponseDTO> sedes() {
        return sedeService.listar();
    }

    @GetMapping("/tipos-eleccion")
    public List<TipoEleccionResponseDTO> tiposEleccion() {
        return tipoEleccionService.listar();
    }
}