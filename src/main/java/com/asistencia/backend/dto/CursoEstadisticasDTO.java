package com.asistencia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CursoEstadisticasDTO {
    private String curso;
    private Integer sesionesRealizadas;
    private Double promedioAsistencia;
}
