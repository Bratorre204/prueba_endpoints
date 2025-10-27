package com.asistencia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstadisticasSesionDTO {
    private Long totalInscritos;
    private Long totalFirmaron;
    private Long presentes;
    private Long ausentes;
    private Long tardios;
    private Long fueraRango;
    private Double porcentajeAsistencia;
}
