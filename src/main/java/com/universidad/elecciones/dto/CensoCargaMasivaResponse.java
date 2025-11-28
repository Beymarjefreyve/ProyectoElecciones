package com.universidad.elecciones.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CensoCargaMasivaResponse {
    private int totalProcesados;
    private int agregados;
    private int yaExistentes;
    private int errores;
    private List<String> mensajes;
}

