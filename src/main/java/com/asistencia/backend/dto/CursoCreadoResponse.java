package com.asistencia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CursoCreadoResponse {
    private Long id;
    private String codigo;
    private String nombre;
    private String turno;
    private String seccion;
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
    }
}
