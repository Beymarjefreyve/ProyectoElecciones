package com.universidad.elecciones.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.universidad.elecciones.dto.ConteoResultadosDTO;
import com.universidad.elecciones.dto.ParticipacionDTO;
import com.universidad.elecciones.dto.VotoRequest;
import com.universidad.elecciones.dto.VotoResponse;
import com.universidad.elecciones.entity.*;
import com.universidad.elecciones.repository.CandidatoRepository;
import com.universidad.elecciones.repository.CensoRepository;
import com.universidad.elecciones.repository.EleccionRepository;
import com.universidad.elecciones.repository.ResultadoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResultadoService {

	@Autowired
    private final ResultadoRepository repo;
	@Autowired
    private final EleccionRepository eleccionRepo;
	@Autowired
    private final CandidatoRepository candidatoRepo;
	@Autowired
    private final CensoRepository censoRepo;

    // ======================================================
    // POST /elecciones/{id}/votar
    // ======================================================
    public VotoResponse votar(Long eleccionId, VotoRequest req, String ip) {

        Eleccion eleccion = eleccionRepo.findById(eleccionId)
                .orElseThrow(() -> new RuntimeException("Elección no existe"));

        Candidato candidato = candidatoRepo.findById(req.getCandidatoId())
                .orElseThrow(() -> new RuntimeException("Candidato no existe"));

        // Validar que el votante pertenece al censo de la elección
        Censo censo = censoRepo
                .findByVotanteDocumentoAndEleccionId(req.getDocumento(), eleccionId)
                .orElseThrow(() -> new RuntimeException("El votante no está habilitado para votar en esta elección"));

        // Validar voto repetido
        if (repo.existsByEleccionIdAndCensoId(eleccionId, censo.getId())) {
            throw new RuntimeException("Este votante ya votó");
        }

        Resultado resultado = Resultado.builder()
                .eleccion(eleccion)
                .candidato(candidato)
                .censo(censo)
                .documento(req.getDocumento())
                .fechaCreacion(LocalDateTime.now())
                .build();

        Resultado saved = repo.save(resultado);

        return VotoResponse.builder()
                .id(saved.getId())
                .candidatoId(candidato.getId())
                .candidatoNombre(candidato.getNombre())
                .eleccionId(eleccionId)
                .documento(saved.getDocumento())
                .fecha(saved.getFechaCreacion())
                .build();
    }

    // ======================================================
    // GET /elecciones/{id}/votos
    // ======================================================
    public List<VotoResponse> listarVotos(Long eleccionId) {

        eleccionRepo.findById(eleccionId)
                .orElseThrow(() -> new RuntimeException("Elección no existe"));

        return repo.findByEleccionId(eleccionId)
                .stream()
                .map(r -> VotoResponse.builder()
                        .id(r.getId())
                        .candidatoId(r.getCandidato().getId())
                        .candidatoNombre(r.getCandidato().getNombre())
                        .eleccionId(eleccionId)
                        .documento(r.getDocumento())
                        .fecha(r.getFechaCreacion())
                        .build())
                .toList();
    }
    
    
 // ========================================================
 // CONTEO DE VOTOS POR CANDIDATO
 // GET /elecciones/{id}/resultados
 // ========================================================
 public List<ConteoResultadosDTO> conteoPorEleccion(Long eleccionId) {

     // Traer todos los resultados de esa elección
     List<Resultado> resultados = repo.findByEleccionId(eleccionId);

     // Agrupar por candidato
     return resultados.stream()
             .collect(Collectors.groupingBy(
                     r -> r.getCandidato(),
                     Collectors.counting()
             ))
             .entrySet()
             .stream()
             .map(entry -> ConteoResultadosDTO.builder()
                     .candidatoId(entry.getKey().getId())
                     .candidatoNombre(entry.getKey().getNombre())
                     .votos(entry.getValue())
                     .build()
             )
             .collect(Collectors.toList());
 }


 // ========================================================
 // PARTICIPACIÓN
 // GET /elecciones/{id}/participacion
 // ========================================================
 public ParticipacionDTO calcularParticipacion(Long eleccionId) {

     long inscritos = censoRepo.countByEleccionId(eleccionId);
     long votantes = repo.countByEleccionId(eleccionId);

     double participacion = inscritos == 0 ? 0.0 :
             (votantes * 100.0) / inscritos;

     return ParticipacionDTO.builder()
             .eleccionId(eleccionId)
             .inscritos(inscritos)
             .votantes(votantes)
             .participacion(Math.round(participacion * 100.0) / 100.0)
             .build();
 }
    
    
    
}
