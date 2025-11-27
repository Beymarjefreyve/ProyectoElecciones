package com.universidad.elecciones.repository;

import com.universidad.elecciones.entity.Candidato;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidatoRepository extends JpaRepository<Candidato, Long> {
}
