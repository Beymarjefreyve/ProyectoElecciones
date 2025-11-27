package com.universidad.elecciones.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "votante")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Votante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "documento", nullable = false, length = 50, unique = true)
    private String documento;

    @Column(name = "nombre", nullable = false, length = 300)
    private String nombre;

}
