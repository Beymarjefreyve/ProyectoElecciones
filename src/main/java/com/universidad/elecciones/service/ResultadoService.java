package com.universidad.elecciones.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.universidad.elecciones.dto.*;
import com.universidad.elecciones.entity.*;
import com.universidad.elecciones.repository.*;

import java.time.LocalDateTime;
import java.util.*;
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
	@Autowired
    private final EleccionService eleccionService;
	@Autowired
    private final VotanteRepository votanteRepo;

    // ======================================================
    // POST /elecciones/{id}/votar
    // ======================================================
    public VotoResponse votar(Long eleccionId, VotoRequest req, String ip) {

        Eleccion eleccion = eleccionRepo.findById(eleccionId)
                .orElseThrow(() -> new RuntimeException("Elección no existe"));

        // Validar que la elección esté abierta
        try {
            eleccionService.validarEleccionAbierta(eleccionId);
        } catch (RuntimeException e) {
            throw new RuntimeException("No se puede votar: " + e.getMessage());
        }

        // Validar fechas de elección
        LocalDateTime ahora = LocalDateTime.now();
        if (ahora.isBefore(eleccion.getFechaInicio())) {
            throw new RuntimeException("La elección aún no ha comenzado. Fecha de inicio: " + eleccion.getFechaInicio());
        }
        if (ahora.isAfter(eleccion.getFechaFinaliza())) {
            throw new RuntimeException("La elección ya ha finalizado. Fecha de finalización: " + eleccion.getFechaFinaliza());
        }

        Candidato candidato = candidatoRepo.findById(req.getCandidatoId())
                .orElseThrow(() -> new RuntimeException("Candidato no existe"));

        // Validar que el votante pertenece al censo de la elección
        // Si no está registrado, lo agregamos automáticamente para permitir la votación
        Censo censo = censoRepo
                .findByVotanteDocumentoAndEleccionId(req.getDocumento(), eleccionId)
                .orElseGet(() -> registrarAutomaticamenteEnCenso(req.getDocumento(), eleccion));

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

    // ======================================================
    // ANULAR VOTO
    // ======================================================
    public void anularVoto(Long eleccionId, String documento) {
        // Validar que la elección existe
        eleccionRepo.findById(eleccionId)
                .orElseThrow(() -> new RuntimeException("Elección no encontrada"));

        // Buscar el censo del votante
        Censo censo = censoRepo
                .findByVotanteDocumentoAndEleccionId(documento, eleccionId)
                .orElseThrow(() -> new RuntimeException("El votante no está registrado en el censo de esta elección"));

        // Buscar el resultado (voto)
        Resultado resultado = repo.findByEleccionIdAndCensoId(eleccionId, censo.getId())
                .orElseThrow(() -> new RuntimeException("No se encontró un voto para este votante en esta elección"));

        // Eliminar el voto (anular)
        repo.delete(resultado);
    }

    // ======================================================
    // ESTADÍSTICAS DETALLADAS (POR FACULTAD, PROGRAMA, SEDE)
    // ======================================================
    public EstadisticasDetalladasDTO estadisticasDetalladas(Long eleccionId) {
        Eleccion eleccion = eleccionRepo.findById(eleccionId)
                .orElseThrow(() -> new RuntimeException("Elección no encontrada"));

        List<Resultado> resultados = repo.findByEleccionId(eleccionId);

        // Estadísticas por facultad
        List<ConteoPorFacultadDTO> porFacultad = calcularPorFacultad(resultados);

        // Estadísticas por programa
        List<ConteoPorProgramaDTO> porPrograma = calcularPorPrograma(resultados);

        // Estadísticas por sede
        List<ConteoPorSedeDTO> porSede = calcularPorSede(resultados);

        return EstadisticasDetalladasDTO.builder()
                .eleccionId(eleccionId)
                .eleccionNombre(eleccion.getNombre())
                .totalVotos((long) resultados.size())
                .porFacultad(porFacultad)
                .porPrograma(porPrograma)
                .porSede(porSede)
                .build();
    }

    // ======================================================
    // EXPORTAR RESULTADOS (CSV)
    // ======================================================
    public String exportarResultadosCSV(Long eleccionId) {
        eleccionRepo.findById(eleccionId)
                .orElseThrow(() -> new RuntimeException("Elección no encontrada"));

        List<Resultado> resultados = repo.findByEleccionId(eleccionId);

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Documento,Candidato ID,Candidato Nombre,Fecha Voto\n");

        for (Resultado r : resultados) {
            csv.append(r.getId()).append(",")
               .append(r.getDocumento()).append(",")
               .append(r.getCandidato().getId()).append(",")
               .append("\"").append(r.getCandidato().getNombre()).append("\",")
               .append(r.getFechaCreacion()).append("\n");
        }

        return csv.toString();
    }

    // ======================================================
    // HISTORIAL DE VOTOS POR VOTANTE
    // ======================================================
    public List<VotoResponse> historialVotosPorVotante(String documento) {
        List<Resultado> resultados = repo.findByDocumento(documento);

        return resultados.stream()
                .map(r -> VotoResponse.builder()
                        .id(r.getId())
                        .candidatoId(r.getCandidato().getId())
                        .candidatoNombre(r.getCandidato().getNombre())
                        .eleccionId(r.getEleccion().getId())
                        .documento(r.getDocumento())
                        .fecha(r.getFechaCreacion())
                        .build())
                .collect(Collectors.toList());
    }

    // ======================================================
    // MÉTODOS PRIVADOS AUXILIARES
    // ======================================================
    private Censo registrarAutomaticamenteEnCenso(String documento, Eleccion eleccion) {
        Votante votante = votanteRepo.findByDocumento(documento)
                .orElseThrow(() -> new RuntimeException("El documento " + documento + " no corresponde a un votante registrado"));

        // Si otro proceso ya lo registró mientras tanto, reutilizamos el registro existente
        Optional<Censo> existente = censoRepo.findByVotanteIdAndEleccionId(votante.getId(), eleccion.getId());
        if (existente.isPresent()) {
            return existente.get();
        }

        Censo nuevo = Censo.builder()
                .votante(votante)
                .eleccion(eleccion)
                .build();

        return censoRepo.save(nuevo);
    }

    private List<ConteoPorFacultadDTO> calcularPorFacultad(List<Resultado> resultados) {

        // Filtrar resultados de esta elección (ya están filtrados)
        Map<Long, List<Resultado>> porFacultad = resultados.stream()
                .filter(r -> r.getEleccion().getFacultad() != null)
                .collect(Collectors.groupingBy(r -> r.getEleccion().getFacultad().getId()));

        return porFacultad.entrySet().stream()
                .map(entry -> {
                    Long facultadId = entry.getKey();
                    String facultadNombre = entry.getValue().get(0).getEleccion().getFacultad().getNombre();
                    List<Resultado> votos = entry.getValue();

                    Map<Candidato, Long> votosPorCandidato = votos.stream()
                            .collect(Collectors.groupingBy(
                                    Resultado::getCandidato,
                                    Collectors.counting()
                            ));

                    List<ConteoResultadosDTO> conteo = votosPorCandidato.entrySet().stream()
                            .map(e -> ConteoResultadosDTO.builder()
                                    .candidatoId(e.getKey().getId())
                                    .candidatoNombre(e.getKey().getNombre())
                                    .votos(e.getValue())
                                    .build())
                            .collect(Collectors.toList());

                    return ConteoPorFacultadDTO.builder()
                            .facultadId(facultadId)
                            .facultadNombre(facultadNombre)
                            .totalVotos((long) votos.size())
                            .votosPorCandidato(conteo)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<ConteoPorProgramaDTO> calcularPorPrograma(List<Resultado> resultados) {

        Map<Long, List<Resultado>> porPrograma = resultados.stream()
                .filter(r -> r.getEleccion().getPrograma() != null)
                .collect(Collectors.groupingBy(r -> r.getEleccion().getPrograma().getId()));

        return porPrograma.entrySet().stream()
                .map(entry -> {
                    Long programaId = entry.getKey();
                    String programaNombre = entry.getValue().get(0).getEleccion().getPrograma().getNombre();
                    List<Resultado> votos = entry.getValue();

                    Map<Candidato, Long> votosPorCandidato = votos.stream()
                            .collect(Collectors.groupingBy(
                                    Resultado::getCandidato,
                                    Collectors.counting()
                            ));

                    List<ConteoResultadosDTO> conteo = votosPorCandidato.entrySet().stream()
                            .map(e -> ConteoResultadosDTO.builder()
                                    .candidatoId(e.getKey().getId())
                                    .candidatoNombre(e.getKey().getNombre())
                                    .votos(e.getValue())
                                    .build())
                            .collect(Collectors.toList());

                    return ConteoPorProgramaDTO.builder()
                            .programaId(programaId)
                            .programaNombre(programaNombre)
                            .totalVotos((long) votos.size())
                            .votosPorCandidato(conteo)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<ConteoPorSedeDTO> calcularPorSede(List<Resultado> resultados) {

        Map<Long, List<Resultado>> porSede = resultados.stream()
                .filter(r -> r.getEleccion().getSede() != null)
                .collect(Collectors.groupingBy(r -> r.getEleccion().getSede().getId()));

        return porSede.entrySet().stream()
                .map(entry -> {
                    Long sedeId = entry.getKey();
                    String sedeNombre = entry.getValue().get(0).getEleccion().getSede().getNombre();
                    List<Resultado> votos = entry.getValue();

                    Map<Candidato, Long> votosPorCandidato = votos.stream()
                            .collect(Collectors.groupingBy(
                                    Resultado::getCandidato,
                                    Collectors.counting()
                            ));

                    List<ConteoResultadosDTO> conteo = votosPorCandidato.entrySet().stream()
                            .map(e -> ConteoResultadosDTO.builder()
                                    .candidatoId(e.getKey().getId())
                                    .candidatoNombre(e.getKey().getNombre())
                                    .votos(e.getValue())
                                    .build())
                            .collect(Collectors.toList());

                    return ConteoPorSedeDTO.builder()
                            .sedeId(sedeId)
                            .sedeNombre(sedeNombre)
                            .totalVotos((long) votos.size())
                            .votosPorCandidato(conteo)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
