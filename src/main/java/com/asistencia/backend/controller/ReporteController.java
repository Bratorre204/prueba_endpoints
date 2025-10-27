package com.asistencia.backend.controller;

import com.asistencia.backend.dto.*;
import com.asistencia.backend.response.ApiResponse;
import com.asistencia.backend.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReporteController {
    
    private final ReporteService reporteService;
    
    /**
     * GET /api/reportes/curso/{idCurso}/asistencia
     * Reporte completo de asistencia de un curso
     */
    @GetMapping("/curso/{idCurso}/asistencia")
    public ResponseEntity<?> getReporteAsistenciaCurso(
            @PathVariable Long idCurso,
            @RequestParam(required = false) LocalDate fechaInicio,
            @RequestParam(required = false) LocalDate fechaFin) {
        try {
            ReporteAsistenciaCursoDTO reporte = reporteService
                .getReporteAsistenciaCurso(idCurso, fechaInicio, fechaFin);
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * GET /api/reportes/estudiante/{idEstudiante}/consolidado
     * Reporte consolidado de un estudiante
     */
    @GetMapping("/estudiante/{idEstudiante}/consolidado")
    public ResponseEntity<?> getReporteConsolidadoEstudiante(
            @PathVariable Long idEstudiante,
            @RequestParam(required = false) String periodo) {
        try {
            ReporteConsolidadoEstudianteDTO reporte = reporteService
                .getReporteConsolidadoEstudiante(idEstudiante, periodo);
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * GET /api/reportes/profesor/{idProfesor}/consolidado
     * Reporte consolidado de todos los cursos de un profesor
     */
    @GetMapping("/profesor/{idProfesor}/consolidado")
    public ResponseEntity<?> getReporteConsolidadoProfesor(
            @PathVariable Long idProfesor,
            @RequestParam(required = false) String periodo) {
        try {
            ReporteConsolidadoProfesorDTO reporte = reporteService
                .getReporteConsolidadoProfesor(idProfesor, periodo);
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * GET /api/reportes/exportar/curso/{idCurso}/excel
     * Exportar reporte de curso a Excel
     */
    @GetMapping("/exportar/curso/{idCurso}/excel")
    public ResponseEntity<byte[]> exportarReporteExcel(@PathVariable Long idCurso) {
        try {
            byte[] excel = reporteService.exportarReporteExcel(idCurso);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "reporte_asistencia.xlsx");
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(excel);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * GET /api/reportes/dashboard
     * Dashboard general con estadísticas
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(@RequestParam(required = false) String periodo) {
        try {
            DashboardDTO dashboard = reporteService.getDashboard(periodo);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}
