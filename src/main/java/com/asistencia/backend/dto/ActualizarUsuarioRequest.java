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
public class ActualizarUsuarioRequest {
    
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
    
    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    private String apellido;
    
    @Email(message = "El formato del email no es válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String correo;
    
    @Size(max = 100, message = "El programa no puede exceder 100 caracteres")
    private String programa;
}
