package com.universidad.elecciones.repository;

import com.universidad.elecciones.entity.Programa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgramaRepository extends JpaRepository<Programa, Long> {
    List<Programa> findByFacultadId(Long facultadId);
}
