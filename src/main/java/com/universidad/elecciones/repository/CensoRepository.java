package com.universidad.elecciones.repository;

import com.universidad.elecciones.entity.Censo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CensoRepository extends JpaRepository<Censo, Long> {

    boolean existsByVotanteIdAndEleccionId(Long votanteId, Long eleccionId);

    Optional<Censo> findByVotanteIdAndEleccionId(Long votanteId, Long eleccionId);

    // NUEVO MÉTODO → buscar censo por documento del votante y elección
    Optional<Censo> findByVotanteDocumentoAndEleccionId(String documento, Long eleccionId);
    
    long countByEleccionId(Long eleccionId);
}