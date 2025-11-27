package com.universidad.elecciones.repository;

import com.universidad.elecciones.entity.Votante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotanteRepository extends JpaRepository<Votante, Long> {
    boolean existsByDocumento(String documento);
}
