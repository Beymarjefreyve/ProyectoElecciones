package com.universidad.elecciones.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitud")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK programa
    @ManyToOne(optional = false)
    @JoinColumn(name = "programa_id", nullable = false)
    private Programa programa;

    // FK sede
    @ManyToOne(optional = false)
    @JoinColumn(name = "sede_id", nullable = false)
    private Sede sede;

    // FK tipo_solicitud
    @ManyToOne(optional = false)
    @JoinColumn(name = "tipo_solicitud_id", nullable = false)
    private TipoSolicitud tipoSolicitud;

    @Column(name = "documento", nullable = false, length = 50)
    private String documento;

    @Column(name = "nombre", nullable = false, length = 300)
    private String nombre;

    @Column(name = "anio", nullable = false)
    private Integer anio;

    @Column(name = "semestre", nullable = false)
    private Integer semestre;

    @Column(name = "email", length = 200)
    private String email;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;
}
