package com.universidad.elecciones.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidatoRequestDTO {
    private String documento;
    private String nombre;
    private String imagen;
}

