package com.asistencia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CursoResponse {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String turno;
    private String seccion;
    private String aula;
    private String horario;
    private Integer año;
    private Integer semestre;
    private String periodo;
    private AsignaturaInfo asignatura;
    private ProfesorInfo profesor;

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
    public static class ProfesorInfo {
        private Long id;
        private String nombre;
        private String apellidos;
        private String correo;
    }
}
