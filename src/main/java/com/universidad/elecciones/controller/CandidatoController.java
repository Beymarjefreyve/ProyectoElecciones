package com.universidad.elecciones.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.universidad.elecciones.entity.Candidato;
import com.universidad.elecciones.service.CandidatoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/candidatos")
@RequiredArgsConstructor
public class CandidatoController {

	@Autowired
    CandidatoService service;

    @GetMapping
    public List<Candidato> listar() {
        return service.listar();
    }

    @PostMapping
    public Candidato crear(@RequestBody Candidato c) {
        return service.crear(c);
    }
}
