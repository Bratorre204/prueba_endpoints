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
public class SesionCreadaResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private String aula;
    private String estado;
    private Double radioProximidad;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private CursoInfo curso;
    private ProfesorInfo profesor;

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
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProfesorInfo {
        private Long id;
        private String identificacion;
        private String nombre;
        private String apellidos;
        private String correo;
    }
}
