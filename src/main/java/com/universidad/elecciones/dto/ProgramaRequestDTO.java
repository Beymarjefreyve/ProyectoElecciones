package com.universidad.elecciones.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramaRequestDTO {
    private String nombre;
    private Long facultadId;
}

