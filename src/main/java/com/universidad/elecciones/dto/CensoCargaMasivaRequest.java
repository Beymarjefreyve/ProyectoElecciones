package com.universidad.elecciones.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CensoCargaMasivaRequest {
    private Long eleccionId;
    private List<Long> votanteIds;
}

