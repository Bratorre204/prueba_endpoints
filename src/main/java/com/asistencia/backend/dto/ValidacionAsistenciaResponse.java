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
public class ValidacionAsistenciaResponse {
    private Boolean puedeFirmar;
    private String estadoValidacion;
    private String mensajeValidacion;
    private InformacionSesion sesion;
    private InformacionEstudiante estudiante;
    private InformacionCurso curso;
    private Restricciones restricciones;
    private RecomendacionesValidacion recomendaciones;

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
        private Boolean sesionActiva;
        private Boolean sesionDisponible;
    }

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
        private Boolean inscritoEnCurso;
        private Boolean inscripcionActiva;
        private Boolean yaFirmo;
        private LocalDateTime fechaUltimaFirma;
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
    public static class Restricciones {
        private Boolean sesionActiva;
        private Boolean sesionDisponible;
        private Boolean estudianteInscrito;
        private Boolean inscripcionActiva;
        private Boolean noHaFirmado;
        private Boolean dentroHorario;
        private Boolean coordenadasValidas;
        private List<String> restriccionesVioladas;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecomendacionesValidacion {
        private String recomendacionGeneral;
        private String recomendacionTiempo;
        private String recomendacionUbicacion;
        private List<String> accionesSugeridas;
        private String proximaOportunidad;
    }
}
