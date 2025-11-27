package com.universidad.elecciones.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConteoResultadosDTO {
    private Long candidatoId;
    private String candidatoNombre;
    private Long votos;
}