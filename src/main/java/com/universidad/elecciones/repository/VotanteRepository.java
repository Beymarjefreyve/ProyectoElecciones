package com.universidad.elecciones.repository;

import com.universidad.elecciones.entity.Votante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VotanteRepository extends JpaRepository<Votante, Long> {
    boolean existsByDocumento(String documento);
    Optional<Votante> findByDocumento(String documento);
}
