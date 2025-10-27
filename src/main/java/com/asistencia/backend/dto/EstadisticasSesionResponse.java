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
public class EstadisticasSesionResponse {
    private Long idSesion;
    private String nombreSesion;
    private String estadoSesion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private LocalDateTime fechaCierre;
    private ResumenCurso curso;
    private ResumenProfesor profesor;
    private EstadisticasDetalladas estadisticas;
    private AnalisisTemporal analisisTemporal;
    private AnalisisGeografico analisisGeografico;
    private Recomendaciones recomendaciones;

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
    public static class EstadisticasDetalladas {
        private Long totalInscritos;
        private Long totalFirmaron;
        private Long presentes;
        private Long ausentes;
        private Long tardios;
        private Long fueraRango;
        private Double porcentajeAsistencia;
        private Double porcentajeFirmaron;
        private Double porcentajeAusentes;
        private Double porcentajeTardios;
        private Long duracionMinutos;
        private String estadoFinal;
        private String calificacionAsistencia;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AnalisisTemporal {
        private LocalDateTime primeraFirma;
        private LocalDateTime ultimaFirma;
        private Long minutosPrimeraFirma;
        private Long minutosUltimaFirma;
        private String patronFirmas;
        private String recomendacionTemporal;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AnalisisGeografico {
        private Double promedioDistancia;
        private Double distanciaMinima;
        private Double distanciaMaxima;
        private Long estudiantesEnRango;
        private Long estudiantesFueraRango;
        private Double porcentajeEnRango;
        private String calidadGeografica;
        private String recomendacionGeografica;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Recomendaciones {
        private String recomendacionGeneral;
        private String recomendacionHorario;
        private String recomendacionUbicacion;
        private String recomendacionComunicacion;
        private List<String> accionesSugeridas;
    }
}
