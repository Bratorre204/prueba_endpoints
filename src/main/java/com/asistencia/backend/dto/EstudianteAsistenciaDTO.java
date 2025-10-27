package com.asistencia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstudianteAsistenciaDTO {
    private String nombre;
    private String identificacion;
    private Integer asistencias;
    private Integer ausencias;
    private Double porcentaje;
}
