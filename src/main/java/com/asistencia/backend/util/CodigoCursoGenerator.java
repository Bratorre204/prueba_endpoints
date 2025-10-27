package com.asistencia.backend.util;

import org.springframework.stereotype.Component;

@Component
public class CodigoCursoGenerator {

    /**
     * Genera el código del curso basado en la estructura:
     * [codigo_asignatura][turno][año][semestre][seccion]
     * Ejemplo: 603D12025B (603 = código asignatura, D = Diurno, 1 = semestre, 2025 = año, B = sección)
     * 
     * @param codigoAsignatura Código de la asignatura (ej: "603")
     * @param turno Turno del curso (DIURNA = "D", NOCTURNA = "N")
     * @param año Año del curso (ej: 2025)
     * @param semestre Semestre (1 o 2)
     * @param seccion Sección del curso (ej: "A", "B", "C")
     * @return Código generado del curso
     */
    public String generarCodigoCurso(String codigoAsignatura, String turno, Integer año, Integer semestre, String seccion) {
        // Convertir turno a código
        String codigoTurno = "D".equals(turno.toUpperCase()) ? "D" : "N";
        
        // Formatear año (tomar últimos 2 dígitos)
        String añoFormateado = String.format("%02d", año % 100);
        
        // Validar semestre
        if (semestre < 1 || semestre > 2) {
            throw new IllegalArgumentException("El semestre debe ser 1 o 2");
        }
        
        // Validar sección
        if (seccion == null || seccion.trim().isEmpty()) {
            seccion = "A"; // Sección por defecto
        }
        
        return String.format("%s%s%d%s%s", 
            codigoAsignatura, 
            codigoTurno, 
            semestre, 
            añoFormateado, 
            seccion.toUpperCase()
        );
    }
    
    /**
     * Parsea un código de curso para extraer sus componentes
     * 
     * @param codigoCurso Código del curso (ej: "603D12025B")
     * @return Objeto con los componentes parseados
     */
    public CodigoCursoParseado parsearCodigoCurso(String codigoCurso) {
        if (codigoCurso == null || codigoCurso.length() < 8) {
            throw new IllegalArgumentException("Código de curso inválido");
        }
        
        try {
            // Estructura: [codigo_asignatura][turno][semestre][año][seccion]
            // Ejemplo: 603D12025B
            String codigoAsignatura = codigoCurso.substring(0, 3); // 603
            String turno = codigoCurso.substring(3, 4); // D o N
            Integer semestre = Integer.parseInt(codigoCurso.substring(4, 5)); // 1 o 2
            String añoStr = codigoCurso.substring(5, 7); // 25
            String seccion = codigoCurso.substring(7); // B
            
            // Convertir año de 2 dígitos a 4 dígitos
            Integer año = 2000 + Integer.parseInt(añoStr);
            
            return new CodigoCursoParseado(codigoAsignatura, turno, semestre, año, seccion);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al parsear código de curso: " + codigoCurso, e);
        }
    }
    
    /**
     * Clase para almacenar los componentes parseados del código
     */
    public static class CodigoCursoParseado {
        private final String codigoAsignatura;
        private final String turno;
        private final Integer semestre;
        private final Integer año;
        private final String seccion;
        
        public CodigoCursoParseado(String codigoAsignatura, String turno, Integer semestre, Integer año, String seccion) {
            this.codigoAsignatura = codigoAsignatura;
            this.turno = turno;
            this.semestre = semestre;
            this.año = año;
            this.seccion = seccion;
        }
        
        // Getters
        public String getCodigoAsignatura() { return codigoAsignatura; }
        public String getTurno() { return turno; }
        public Integer getSemestre() { return semestre; }
        public Integer getAño() { return año; }
        public String getSeccion() { return seccion; }
        
        public String getTurnoCompleto() {
            return "D".equals(turno) ? "DIURNA" : "NOCTURNA";
        }
        
        public String getPeriodo() {
            return año + "-" + semestre;
        }
    }
}
