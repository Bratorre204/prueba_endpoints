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
public class DashboardDTO {
    private Integer totalSesionesHoy;
    private Integer totalEstudiantesActivos;
    private Double promedioAsistenciaGeneral;
    private List<CursoMasActivoDTO> cursosMasActivos;
}
