package com.universidad.elecciones.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipo_solicitud")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;
}
