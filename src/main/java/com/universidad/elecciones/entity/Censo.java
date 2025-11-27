package com.universidad.elecciones.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "censo",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_censo_votante_eleccion", columnNames = {"votante_id", "eleccion_id"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Censo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK votante
    @ManyToOne(optional = false)
    @JoinColumn(name = "votante_id", nullable = false)
    private Votante votante;

    // FK eleccion
    @ManyToOne(optional = false)
    @JoinColumn(name = "eleccion_id", nullable = false)
    private Eleccion eleccion;
}
