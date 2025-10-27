package com.asistencia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReporteConsolidadoEstudianteDTO {
    private String estudiante;
    private List<AsistenciaPorCursoDTO> cursos;
}
