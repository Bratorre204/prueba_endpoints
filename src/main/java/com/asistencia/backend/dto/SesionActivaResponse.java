package com.asistencia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SesionActivaResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private String aula;
    private String estado;
    private Double radioProximidad;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private ProfesorBasico profesor;
    private CursoBasico curso;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProfesorBasico {
        private Long id;
        private String identificacion;
        private String nombre;
        private String apellidos;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CursoBasico {
        private Long id;
        private String codigo;
        private String nombre;
        private String turno;
        private String seccion;
        private String periodo;
    }
}
