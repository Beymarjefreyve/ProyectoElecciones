package com.universidad.elecciones.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipo_eleccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoEleccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;
}
