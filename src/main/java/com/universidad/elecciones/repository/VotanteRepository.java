package com.universidad.elecciones.repository;

import com.universidad.elecciones.entity.Votante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VotanteRepository extends JpaRepository<Votante, Long> {
    boolean existsByDocumento(String documento);

    Optional<Votante> findByDocumento(String documento);

    Optional<Votante> findByEmail(String email);

    Optional<Votante> findByTokenVerificacion(String token);

    boolean existsByEmail(String email);

    List<Votante> findByFacultadId(Long facultadId);
}
