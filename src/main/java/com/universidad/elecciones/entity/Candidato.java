package com.universidad.elecciones.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "candidato")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "documento", nullable = false, length = 50)
    private String documento;

    @Column(name = "nombre", nullable = false, length = 300)
    private String nombre;

    @Column(name = "imagen", length = 255)
    private String imagen;
}
