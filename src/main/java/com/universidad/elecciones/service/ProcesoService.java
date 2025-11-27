package com.universidad.elecciones.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.universidad.elecciones.entity.Proceso;
import com.universidad.elecciones.repository.ProcesoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProcesoService {

	@Autowired
    private final ProcesoRepository repo;

    public Proceso crear(Proceso p) {
        return repo.save(p);
    }

    public List<Proceso> listar() {
        return repo.findAll();
    }
}
