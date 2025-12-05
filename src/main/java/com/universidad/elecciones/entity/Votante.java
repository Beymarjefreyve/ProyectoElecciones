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

    @Column(name = "email", unique = true, length = 200)
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "rol", length = 20)
    private String rol; // ESTUDIANTE, ADMINISTRATIVO

    @Column(name = "email_verificado")
    private Boolean emailVerificado;

    @Column(name = "token_verificacion", length = 100)
    private String tokenVerificacion;

    @ManyToOne
    @JoinColumn(name = "facultad_id")
    private Facultad facultad;

    @Column(name = "estado", length = 20)
    private String estado;
}
