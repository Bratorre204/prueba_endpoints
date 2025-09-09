package com.asistencia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CursoResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private ProfesorInfo profesor;

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
