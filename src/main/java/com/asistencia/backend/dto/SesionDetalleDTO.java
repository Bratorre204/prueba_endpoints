package com.asistencia.backend.dto;

import com.asistencia.backend.model.EstadoAsistencia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SesionDetalleDTO {
    private Long id;
    private LocalDateTime fechaFirma;
    private String estudiante;
    private String curso;
    private EstadoAsistencia estado;
    private Double distanciaMetros;
    private String observacion;
}
