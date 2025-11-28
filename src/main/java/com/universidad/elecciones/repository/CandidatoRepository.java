package com.universidad.elecciones.repository;

import com.universidad.elecciones.entity.Candidato;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CandidatoRepository extends JpaRepository<Candidato, Long> {
    Optional<Candidato> findByDocumento(String documento);
    boolean existsByDocumento(String documento);
}
