package com.asistencia.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CrearCursoRequest {
    
    @NotNull(message = "El ID de la asignatura es obligatorio")
    private Long idAsignatura;
    
    @NotBlank(message = "La descripción del curso es obligatoria")
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;
    
    @NotBlank(message = "El nombre del curso es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
    
    @NotBlank(message = "El turno es obligatorio")
    private String turno; // DIURNA, NOCTURNA
    
    @NotBlank(message = "La sección es obligatoria")
    @Size(max = 5, message = "La sección no puede exceder 5 caracteres")
    private String seccion;
    
    @NotBlank(message = "El aula es obligatoria")
    @Size(max = 50, message = "El aula no puede exceder 50 caracteres")
    private String aula;
    
    @NotBlank(message = "El horario es obligatorio")
    @Size(max = 100, message = "El horario no puede exceder 100 caracteres")
    private String horario;
    
    @NotNull(message = "El año es obligatorio")
    private Integer año;
    
    @NotNull(message = "El semestre es obligatorio")
    private Integer semestre;
    
    @NotNull(message = "El ID del profesor es obligatorio")
    private Long idProfesor;
}
