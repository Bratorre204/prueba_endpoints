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
public class SesionEliminadaResponse {
    private Long idEliminado;
    private String nombreSesion;
    private String estadoEliminacion;
    private LocalDateTime fechaEliminacion;
    private ResumenCurso curso;
    private ResumenProfesor profesor;
    private EstadisticasEliminacion estadisticas;
    private String mensajeEliminacion;

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
    public static class EstadisticasEliminacion {
        private Long totalAsistenciasRegistradas;
        private Long estudiantesAfectados;
        private String impactoEliminacion;
        private Boolean datosPerdidos;
    }
}
