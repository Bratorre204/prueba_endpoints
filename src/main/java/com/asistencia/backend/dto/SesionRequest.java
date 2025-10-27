package com.asistencia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SesionRequest {
    private Long idDocente;
    private Long idCurso;
    private Double latitud;
    private Double longitud;
}
