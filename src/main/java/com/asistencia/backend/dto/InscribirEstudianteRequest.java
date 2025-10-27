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
public class InscribirEstudianteRequest {
    
    @NotNull(message = "El ID del estudiante es obligatorio")
    private Long idEstudiante;
    
    private String observaciones;
}
