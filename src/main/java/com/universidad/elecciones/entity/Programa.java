package com.universidad.elecciones.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "programa")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Programa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @ManyToOne(optional = false)
    @JoinColumn(name = "facultad_id", nullable = false)
    private Facultad facultad;
}
