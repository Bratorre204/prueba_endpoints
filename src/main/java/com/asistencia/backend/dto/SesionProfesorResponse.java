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
public class SesionProfesorResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private String aula;
    private String estado;
    private Double radioProximidad;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private LocalDateTime fechaCierre;
    private CursoInfo curso;
    private EstadisticasBasicas estadisticas;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CursoInfo {
        private Long id;
        private String codigo;
        private String nombre;
        private String turno;
        private String seccion;
        private String periodo;
        private String aula;
        private String horario;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EstadisticasBasicas {
        private Long totalInscritos;
        private Long totalFirmaron;
        private Long presentes;
        private Long ausentes;
        private Long tardios;
        private Long fueraRango;
        private Double porcentajeAsistencia;
    }
}
