package com.universidad.elecciones.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConteoPorFacultadDTO {
    private Long facultadId;
    private String facultadNombre;
    private Long totalVotos;
    private List<ConteoResultadosDTO> votosPorCandidato;
}

