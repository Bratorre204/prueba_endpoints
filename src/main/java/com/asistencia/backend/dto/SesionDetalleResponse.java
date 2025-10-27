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
public class SesionDetalleResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private String aula;
    private String estado;
    private Double radioProximidad;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private LocalDateTime fechaCierre;
    private CoordenadasProfesor coordenadasProfesor;
    private CursoCompleto curso;
    private ProfesorCompleto profesor;
    private EstadisticasDetalladas estadisticas;
    private List<AsistenciaDetalle> asistencias;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CoordenadasProfesor {
        private Double latitud;
        private Double longitud;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CursoCompleto {
        private Long id;
        private String codigo;
        private String nombre;
        private String descripcion;
        private String turno;
        private String seccion;
        private String periodo;
        private String aula;
        private String horario;
        private Integer año;
        private Integer semestre;
        private AsignaturaInfo asignatura;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AsignaturaInfo {
        private Long id;
        private String codigo;
        private String nombre;
        private String descripcion;
        private Integer creditos;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProfesorCompleto {
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
    public static class EstadisticasDetalladas {
        private Long totalInscritos;
        private Long totalFirmaron;
        private Long presentes;
        private Long ausentes;
        private Long tardios;
        private Long fueraRango;
        private Double porcentajeAsistencia;
        private Double porcentajeFirmaron;
        private Long estudiantesEnRango;
        private Long estudiantesFueraRango;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AsistenciaDetalle {
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
    }
}
