package com.universidad.elecciones.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConteoPorSedeDTO {
    private Long sedeId;
    private String sedeNombre;
    private Long totalVotos;
    private List<ConteoResultadosDTO> votosPorCandidato;
}

