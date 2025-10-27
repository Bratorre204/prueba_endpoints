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
public class CrearAsignaturaRequest {
    
    @NotBlank(message = "El código de la asignatura es obligatorio")
    @Size(max = 20, message = "El código no puede exceder 20 caracteres")
    private String codigo;
    
    @NotBlank(message = "El nombre de la asignatura es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;
    
    @Size(max = 10, message = "La abreviatura no puede exceder 10 caracteres")
    private String abreviatura;
    
    @NotNull(message = "Los créditos son obligatorios")
    @Min(value = 1, message = "Los créditos deben ser al menos 1")
    @Max(value = 10, message = "Los créditos no pueden ser mayor a 10")
    private Integer creditos;
}
