package com.universidad.elecciones.repository;

import com.universidad.elecciones.entity.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    List<Inscripcion> findByEleccionId(Long eleccionId);

    boolean existsByCandidatoIdAndEleccionId(Long candidatoId, Long eleccionId);
    
    boolean existsByNumeroAndEleccionId(Integer numero, Long eleccionId);
    
    Optional<Inscripcion> findByNumeroAndEleccionId(Integer numero, Long eleccionId);
}
