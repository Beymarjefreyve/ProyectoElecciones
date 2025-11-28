package com.universidad.elecciones.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CensoEliminacionMasivaRequest {
    private Long eleccionId;
    private List<Long> votanteIds;
}

