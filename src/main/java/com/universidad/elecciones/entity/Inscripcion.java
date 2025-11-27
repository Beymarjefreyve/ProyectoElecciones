package com.universidad.elecciones.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "inscripcion",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_inscripcion_candidato_eleccion", columnNames = {"candidato_id", "eleccion_id"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK candidato
    @ManyToOne(optional = false)
    @JoinColumn(name = "candidato_id", nullable = false)
    private Candidato candidato;

    // FK eleccion
    @ManyToOne(optional = false)
    @JoinColumn(name = "eleccion_id", nullable = false)
    private Eleccion eleccion;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @Column(name = "numero", nullable = false)
    private Integer numero;

}
