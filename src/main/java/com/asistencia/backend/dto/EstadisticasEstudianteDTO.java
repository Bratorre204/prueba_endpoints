package com.asistencia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstadisticasEstudianteDTO {
    private Long totalSesiones;
    private Long asistencias;
    private Long ausencias;
    private Long tardias;
    private Long fueraRango;
    private Double porcentajeAsistencia;
    private String curso;
}
