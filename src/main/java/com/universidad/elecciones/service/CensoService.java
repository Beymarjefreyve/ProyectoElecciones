package com.universidad.elecciones.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.universidad.elecciones.dto.CensoRequest;
import com.universidad.elecciones.dto.CensoResponse;
import com.universidad.elecciones.entity.Censo;
import com.universidad.elecciones.repository.CensoRepository;
import com.universidad.elecciones.repository.EleccionRepository;
import com.universidad.elecciones.repository.VotanteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CensoService {

	@Autowired
    CensoRepository repo;
	@Autowired
    VotanteRepository votanteRepo;
	@Autowired
    EleccionRepository eleccionRepo;

    // ==========================================================
    // NUEVO: AGREGAR VOTANTE AL CENSO POR ELECCIÓN (POST)
    // ==========================================================
    public CensoResponse agregar(CensoRequest req) {

        var votante = votanteRepo.findById(req.getVotanteId())
                .orElseThrow(() -> new RuntimeException("Votante no existe"));

        var eleccion = eleccionRepo.findById(req.getEleccionId())
                .orElseThrow(() -> new RuntimeException("Elección no existe"));

        if (repo.existsByVotanteIdAndEleccionId(req.getVotanteId(), req.getEleccionId())) {
            throw new RuntimeException("Este votante ya está registrado en esta elección");
        }

        // Crear entidad Censo
        Censo c = Censo.builder()
                .votante(votante)
                .eleccion(eleccion)
                .build();

        Censo guardado = repo.save(c);

        // Convertir manualmente a Response
        return CensoResponse.builder()
                .id(guardado.getId())
                .votanteId(guardado.getVotante().getId())
                .eleccionId(guardado.getEleccion().getId())
                .build();
    }

    // ==========================================================
    // NUEVO: LISTAR CENSO DE UNA ELECCIÓN (GET)
    // ==========================================================
    public List<CensoResponse> listarPorEleccion(Long eleccionId) {

        eleccionRepo.findById(eleccionId)
                .orElseThrow(() -> new RuntimeException("La elección no existe"));

        return repo.findAll().stream()
                .filter(c -> c.getEleccion().getId().equals(eleccionId))
                .map(c -> CensoResponse.builder()
                        .id(c.getId())
                        .votanteId(c.getVotante().getId())
                        .eleccionId(c.getEleccion().getId())
                        .build())
                .collect(Collectors.toList());
    }

   
  
}