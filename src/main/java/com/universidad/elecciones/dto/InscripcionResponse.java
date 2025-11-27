package com.universidad.elecciones.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscripcionResponse {
    private Long id;
    private Long candidatoId;
    private String candidatoNombre;
    private String candidatoImagen;
    private Integer numero;
    private LocalDateTime fecha;
    private String estado;
}