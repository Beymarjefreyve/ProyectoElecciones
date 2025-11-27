package com.universidad.elecciones.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.universidad.elecciones.entity.Proceso;
import com.universidad.elecciones.service.ProcesoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/procesos")
@RequiredArgsConstructor
public class ProcesoController {

	@Autowired
   ProcesoService service;

    @GetMapping
    public List<Proceso> listar() {
        return service.listar();
    }

    @PostMapping
    public Proceso crear(@RequestBody Proceso p) {
        return service.crear(p);
    }
}
