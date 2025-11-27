package com.universidad.elecciones.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "eleccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Eleccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK tipo_eleccion
    @ManyToOne(optional = false)
    @JoinColumn(name = "tipo_eleccion_id", nullable = false)
    private TipoEleccion tipoEleccion;

    // FK tipo
    @ManyToOne(optional = false)
    @JoinColumn(name = "tipo_id", nullable = false)
    private Tipo tipo;

    // FK programa (nullable)
    @ManyToOne
    @JoinColumn(name = "programa_id")
    private Programa programa;

    @Column(name = "nombre", nullable = false, length = 300)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "anio", nullable = false)
    private Integer anio;

    @Column(name = "semestre", nullable = false)
    private Integer semestre;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_finaliza", nullable = false)
    private LocalDateTime fechaFinaliza;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    // FK sede (nullable)
    @ManyToOne
    @JoinColumn(name = "sede_id")
    private Sede sede;

    @Column(name = "extendido")
    private Boolean extendido;

    // FK proceso (NOT NULL)
    @ManyToOne(optional = false)
    @JoinColumn(name = "proceso_id", nullable = false)
    private Proceso proceso;

    // FK facultad (nullable)
    @ManyToOne
    @JoinColumn(name = "facultad_id")
    private Facultad facultad;
}
