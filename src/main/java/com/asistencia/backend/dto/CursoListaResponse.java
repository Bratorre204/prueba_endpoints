package com.asistencia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CursoListaResponse {
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
    private AsignaturaBasica asignatura;
    private ProfesorBasico profesor;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AsignaturaBasica {
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
    public static class ProfesorBasico {
        private Long id;
        private String identificacion;
        private String nombre;
        private String apellidos;
        private String correo;
    }
}
