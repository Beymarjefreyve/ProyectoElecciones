package com.universidad.elecciones.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "resultado",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_resultado_censo_eleccion", columnNames = {"censo_id", "eleccion_id"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resultado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK eleccion
    @ManyToOne(optional = false)
    @JoinColumn(name = "eleccion_id", nullable = false)
    private Eleccion eleccion;

    // FK candidato
    @ManyToOne(optional = false)
    @JoinColumn(name = "candidato_id", nullable = false)
    private Candidato candidato;

    // FK censo
    @ManyToOne(optional = false)
    @JoinColumn(name = "censo_id", nullable = false)
    private Censo censo;

    @Column(name = "documento", nullable = false, length = 50)
    private String documento;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

}
