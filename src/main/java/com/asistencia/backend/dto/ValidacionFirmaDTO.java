package com.asistencia.backend.dto;

import com.asistencia.backend.model.Sesion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidacionFirmaDTO {
    private boolean puedeFirmar;
    private String mensaje;
    private Sesion sesion;
}
