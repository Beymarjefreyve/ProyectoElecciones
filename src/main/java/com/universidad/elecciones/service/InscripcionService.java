package com.universidad.elecciones.service;

import com.universidad.elecciones.dto.InscripcionRequest;
import com.universidad.elecciones.dto.InscripcionResponse;
import com.universidad.elecciones.entity.Candidato;
import com.universidad.elecciones.entity.Eleccion;
import com.universidad.elecciones.entity.Inscripcion;
import com.universidad.elecciones.repository.CandidatoRepository;
import com.universidad.elecciones.repository.EleccionRepository;
import com.universidad.elecciones.repository.InscripcionRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InscripcionService {

	@Autowired
    private final InscripcionRepository repo;
	@Autowired
    private final EleccionRepository eleccionRepo;
	@Autowired
    private final CandidatoRepository candidatoRepo;

    // -----------------------------------------
    // POST /elecciones/{id}/inscripciones
    // -----------------------------------------
    public InscripcionResponse inscribir(Long eleccionId, InscripcionRequest request) {

        Eleccion eleccion = eleccionRepo.findById(eleccionId)
                .orElseThrow(() -> new RuntimeException("Elección no existe"));

        Candidato candidato = candidatoRepo.findById(request.getCandidatoId())
                .orElseThrow(() -> new RuntimeException("Candidato no existe"));

        // validar si ya se inscribió antes
        if (repo.existsByCandidatoIdAndEleccionId(request.getCandidatoId(), eleccionId)) {
            throw new RuntimeException("Este candidato ya está inscrito en esta elección");
        }

        Inscripcion ins = Inscripcion.builder()
                .eleccion(eleccion)
                .candidato(candidato)
                .numero(request.getNumero())
                .estado("ACTIVO")
                .fecha(LocalDateTime.now())
                .build();

        Inscripcion saved = repo.save(ins);

        return InscripcionResponse.builder()
                .id(saved.getId())
                .candidatoId(candidato.getId())
                .candidatoNombre(candidato.getNombre())
                .candidatoImagen(candidato.getImagen())
                .numero(saved.getNumero())
                .fecha(saved.getFecha())
                .estado(saved.getEstado())
                .build();
    }

    // -----------------------------------------
    // GET /elecciones/{id}/candidatos
    // -----------------------------------------
    public List<InscripcionResponse> listarCandidatos(Long eleccionId) {

        eleccionRepo.findById(eleccionId)
                .orElseThrow(() -> new RuntimeException("Elección no existe"));

        return repo.findByEleccionId(eleccionId).stream()
                .map(ins -> InscripcionResponse.builder()
                        .id(ins.getId())
                        .candidatoId(ins.getCandidato().getId())
                        .candidatoNombre(ins.getCandidato().getNombre())
                        .candidatoImagen(ins.getCandidato().getImagen())
                        .numero(ins.getNumero())
                        .fecha(ins.getFecha())
                        .estado(ins.getEstado())
                        .build())
                .toList();
    }
}