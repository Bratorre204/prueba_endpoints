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
public class ReporteAsistenciaCursoDTO {
    private String curso;
    private String periodo;
    private List<EstudianteAsistenciaDTO> estudiantes;
    private EstadisticasGeneralesDTO estadisticas;
}
