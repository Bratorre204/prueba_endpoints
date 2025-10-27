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
public class SesionCerradaResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private String aula;
    private String estado;
    private Double radioProximidad;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private LocalDateTime fechaCierre;
    private ResumenCurso curso;
    private ResumenProfesor profesor;
    private EstadisticasFinales estadisticas;
    private ResumenAsistencia resumenAsistencia;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResumenCurso {
        private Long id;
        private String codigo;
        private String nombre;
        private String turno;
        private String seccion;
        private String periodo;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResumenProfesor {
        private Long id;
        private String identificacion;
        private String nombre;
        private String apellidos;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EstadisticasFinales {
        private Long totalInscritos;
        private Long totalFirmaron;
        private Long presentes;
        private Long ausentes;
        private Long tardios;
        private Long fueraRango;
        private Double porcentajeAsistencia;
        private Double porcentajeFirmaron;
        private Long duracionMinutos;
        private String estadoFinal;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResumenAsistencia {
        private Long estudiantesEnRango;
        private Long estudiantesFueraRango;
        private Double promedioDistancia;
        private String mensajeResumen;
        private Boolean asistenciaExitosa;
    }
}
