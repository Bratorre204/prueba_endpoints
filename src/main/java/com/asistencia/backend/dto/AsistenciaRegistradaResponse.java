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
public class AsistenciaRegistradaResponse {
    private Long id;
    private String estado;
    private LocalDateTime fechaFirma;
    private Double distanciaMetros;
    private Boolean enRango;
    private String mensajeValidacion;
    private InformacionEstudiante estudiante;
    private InformacionSesion sesion;
    private InformacionCurso curso;
    private InformacionProfesor profesor;
    private AnalisisAsistencia analisis;
    private RecomendacionesEstudiante recomendaciones;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InformacionEstudiante {
        private Long id;
        private String identificacion;
        private String nombre;
        private String apellidos;
        private String correo;
        private String programa;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InformacionSesion {
        private Long id;
        private String nombre;
        private String estado;
        private LocalDateTime fechaInicio;
        private LocalDateTime fechaFin;
        private Double radioProximidad;
        private String aula;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InformacionCurso {
        private Long id;
        private String codigo;
        private String nombre;
        private String turno;
        private String seccion;
        private String periodo;
        private String horario;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InformacionProfesor {
        private Long id;
        private String identificacion;
        private String nombre;
        private String apellidos;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AnalisisAsistencia {
        private String calidadAsistencia;
        private String puntualidad;
        private String precisionGeografica;
        private String tiempoTranscurrido;
        private Boolean esPrimeraFirma;
        private Integer posicionEnLista;
        private String mensajeAnalisis;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecomendacionesEstudiante {
        private String recomendacionGeneral;
        private String recomendacionUbicacion;
            private String recomendacionTiempo;
        private List<String> accionesSugeridas;
    }
}
