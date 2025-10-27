package com.asistencia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReporteAsistenciaResponse {
    private Long idSesion;
    private String nombreSesion;
    private String estadoSesion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private LocalDateTime fechaCierre;
    private ResumenCurso curso;
    private ResumenProfesor profesor;
    private EstadisticasGenerales estadisticas;
    private List<DetalleAsistencia> asistencias;
    private ResumenGeografico resumenGeografico;

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
        private String aula;
        private String horario;
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
        private String correo;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EstadisticasGenerales {
        private Long totalInscritos;
        private Long totalFirmaron;
        private Long presentes;
        private Long ausentes;
        private Long tardios;
        private Long fueraRango;
        private Double porcentajeAsistencia;
        private Double porcentajeFirmaron;
        private Long duracionMinutos;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetalleAsistencia {
        private Long id;
        private String estudianteNombre;
        private String estudianteIdentificacion;
        private String estudianteCorreo;
        private String estado;
        private LocalDateTime fechaFirma;
        private Double latitudEstudiante;
        private Double longitudEstudiante;
        private Double distanciaMetros;
        private String observacion;
        private Boolean enRango;
        private String tiempoTranscurrido;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResumenGeografico {
        private Double promedioDistancia;
        private Double distanciaMinima;
        private Double distanciaMaxima;
        private Long estudiantesEnRango;
        private Long estudiantesFueraRango;
        private String mensajeGeografico;
    }
}
