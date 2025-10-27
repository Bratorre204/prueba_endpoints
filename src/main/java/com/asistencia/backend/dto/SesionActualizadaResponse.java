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
public class SesionActualizadaResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private String aula;
    private String estado;
    private Double radioProximidad;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private LocalDateTime fechaCierre;
    private CoordenadasActualizadas coordenadasProfesor;
    private ResumenCurso curso;
    private ResumenProfesor profesor;
    private String mensajeActualizacion;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CoordenadasActualizadas {
        private Double latitud;
        private Double longitud;
        private Double radioProximidad;
        private LocalDateTime fechaActualizacion;
    }

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
}
