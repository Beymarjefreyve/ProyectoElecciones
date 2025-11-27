package com.universidad.elecciones.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.universidad.elecciones.entity.Candidato;
import com.universidad.elecciones.repository.CandidatoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CandidatoService {

	@Autowired
	CandidatoRepository repo;

    public Candidato crear(Candidato c) {
        return repo.save(c);
    }

    public List<Candidato> listar() {
        return repo.findAll();
    }
}
