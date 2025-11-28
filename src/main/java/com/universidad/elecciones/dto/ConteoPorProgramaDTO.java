package com.universidad.elecciones.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConteoPorProgramaDTO {
    private Long programaId;
    private String programaNombre;
    private Long totalVotos;
    private List<ConteoResultadosDTO> votosPorCandidato;
}

