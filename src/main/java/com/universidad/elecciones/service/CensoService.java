package com.universidad.elecciones.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.universidad.elecciones.dto.CensoCargaMasivaRequest;
import com.universidad.elecciones.dto.CensoCargaMasivaResponse;
import com.universidad.elecciones.dto.CensoEliminacionMasivaRequest;
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
    // AGREGAR VOTANTE AL CENSO POR ELECCIÓN (POST)
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
    // LISTAR CENSO DE UNA ELECCIÓN (GET)
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

    // ==========================================================
    // ELIMINAR DEL CENSO
    // ==========================================================
    public void eliminar(Long votanteId, Long eleccionId) {
        Censo censo = repo.findByVotanteIdAndEleccionId(votanteId, eleccionId)
                .orElseThrow(() -> new RuntimeException("El votante no está registrado en el censo de esta elección"));
        repo.delete(censo);
    }

    // ==========================================================
    // BUSCAR POR VOTANTE Y ELECCIÓN
    // ==========================================================
    public CensoResponse buscarPorVotanteYEleccion(Long votanteId, Long eleccionId) {
        Censo censo = repo.findByVotanteIdAndEleccionId(votanteId, eleccionId)
                .orElseThrow(() -> new RuntimeException("El votante no está registrado en el censo de esta elección"));
        
        return CensoResponse.builder()
                .id(censo.getId())
                .votanteId(censo.getVotante().getId())
                .eleccionId(censo.getEleccion().getId())
                .build();
    }

    // ==========================================================
    // VALIDAR SI UN VOTANTE ESTÁ EN EL CENSO
    // ==========================================================
    public boolean validarSiEstaEnCenso(Long votanteId, Long eleccionId) {
        // Validar que el votante existe
        votanteRepo.findById(votanteId)
                .orElseThrow(() -> new RuntimeException("Votante no encontrado"));
        
        // Validar que la elección existe
        eleccionRepo.findById(eleccionId)
                .orElseThrow(() -> new RuntimeException("Elección no encontrada"));
        
        return repo.existsByVotanteIdAndEleccionId(votanteId, eleccionId);
    }

    // ==========================================================
    // CARGA MASIVA DE VOTANTES
    // ==========================================================
    @Transactional
    public CensoCargaMasivaResponse cargaMasiva(CensoCargaMasivaRequest request) {
        // Validar que la elección existe
        var eleccion = eleccionRepo.findById(request.getEleccionId())
                .orElseThrow(() -> new RuntimeException("Elección no encontrada"));

        if (request.getVotanteIds() == null || request.getVotanteIds().isEmpty()) {
            throw new RuntimeException("La lista de votantes no puede estar vacía");
        }

        int agregados = 0;
        int yaExistentes = 0;
        int errores = 0;
        List<String> mensajes = new ArrayList<>();

        for (Long votanteId : request.getVotanteIds()) {
            try {
                // Validar que el votante existe
                var votante = votanteRepo.findById(votanteId)
                        .orElseThrow(() -> new RuntimeException("Votante con ID " + votanteId + " no encontrado"));

                // Verificar si ya existe
                if (repo.existsByVotanteIdAndEleccionId(votanteId, request.getEleccionId())) {
                    yaExistentes++;
                    mensajes.add("Votante ID " + votanteId + " ya está en el censo");
                } else {
                    // Agregar al censo
                    Censo censo = Censo.builder()
                            .votante(votante)
                            .eleccion(eleccion)
                            .build();
                    repo.save(censo);
                    agregados++;
                    mensajes.add("Votante ID " + votanteId + " agregado exitosamente");
                }
            } catch (Exception e) {
                errores++;
                mensajes.add("Error con votante ID " + votanteId + ": " + e.getMessage());
            }
        }

        return CensoCargaMasivaResponse.builder()
                .totalProcesados(request.getVotanteIds().size())
                .agregados(agregados)
                .yaExistentes(yaExistentes)
                .errores(errores)
                .mensajes(mensajes)
                .build();
    }

    // ==========================================================
    // ELIMINAR MÚLTIPLES VOTANTES DEL CENSO
    // ==========================================================
    @Transactional
    public CensoCargaMasivaResponse eliminarMultiples(CensoEliminacionMasivaRequest request) {
        // Validar que la elección existe
        eleccionRepo.findById(request.getEleccionId())
                .orElseThrow(() -> new RuntimeException("Elección no encontrada"));

        if (request.getVotanteIds() == null || request.getVotanteIds().isEmpty()) {
            throw new RuntimeException("La lista de votantes no puede estar vacía");
        }

        int eliminados = 0;
        int noEncontrados = 0;
        int errores = 0;
        List<String> mensajes = new ArrayList<>();

        for (Long votanteId : request.getVotanteIds()) {
            try {
                Optional<Censo> censoOpt = repo.findByVotanteIdAndEleccionId(votanteId, request.getEleccionId());
                
                if (censoOpt.isPresent()) {
                    repo.delete(censoOpt.get());
                    eliminados++;
                    mensajes.add("Votante ID " + votanteId + " eliminado exitosamente");
                } else {
                    noEncontrados++;
                    mensajes.add("Votante ID " + votanteId + " no está en el censo");
                }
            } catch (Exception e) {
                errores++;
                mensajes.add("Error eliminando votante ID " + votanteId + ": " + e.getMessage());
            }
        }

        return CensoCargaMasivaResponse.builder()
                .totalProcesados(request.getVotanteIds().size())
                .agregados(eliminados) // Reutilizamos el campo para eliminados
                .yaExistentes(noEncontrados) // Reutilizamos el campo para no encontrados
                .errores(errores)
                .mensajes(mensajes)
                .build();
    }
}