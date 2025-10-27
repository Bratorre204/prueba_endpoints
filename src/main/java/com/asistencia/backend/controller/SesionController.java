package com.asistencia.backend.controller;

import com.asistencia.backend.dto.*;
import com.asistencia.backend.model.Sesion;
import com.asistencia.backend.response.ApiResponse;
import com.asistencia.backend.service.SesionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sesiones")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SesionController {
    
    private final SesionService sesionService;
    
    /**
     * POST /api/sesiones/crear
     * Profesor crea una nueva sesión con sus coordenadas
     */
    @PostMapping("/crear")
    public ResponseEntity<?> crearSesion(@RequestBody @Valid CrearSesionRequest request) {
        try {
            Sesion sesion = sesionService.crearSesion(request);
            SesionCreadaResponse sesionResponse = sesionService.convertirASesionCreadaResponse(sesion);
            return ResponseEntity.ok(new ApiResponse(true, "Sesión creada exitosamente", sesionResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * PUT /api/sesiones/{id}/cerrar
     * Profesor cierra la sesión (no permite más firmas)
     */
    @PutMapping("/{id}/cerrar")
    public ResponseEntity<?> cerrarSesion(@PathVariable Long id) {
        try {
            Sesion sesion = sesionService.cerrarSesion(id);
            SesionCerradaResponse sesionResponse = sesionService.convertirASesionCerradaResponse(sesion);
            return ResponseEntity.ok(new ApiResponse(true, "Sesión cerrada exitosamente", sesionResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * GET /api/sesiones/profesor/{idProfesor}
     * Obtener todas las sesiones de un profesor
     */
    @GetMapping("/profesor/{idProfesor}")
    public ResponseEntity<?> getSesionesPorProfesor(
            @PathVariable Long idProfesor,
            @RequestParam(required = false) String periodo,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            Page<Sesion> sesiones = sesionService.getSesionesPorProfesor(idProfesor, periodo, pageable);
            List<SesionProfesorResponse> sesionesResponse = sesionService.convertirASesionesProfesorResponse(sesiones.getContent());
            
            // Crear respuesta paginada personalizada
            Map<String, Object> response = new HashMap<>();
            response.put("content", sesionesResponse);
            response.put("totalElements", sesiones.getTotalElements());
            response.put("totalPages", sesiones.getTotalPages());
            response.put("size", sesiones.getSize());
            response.put("number", sesiones.getNumber());
            response.put("first", sesiones.isFirst());
            response.put("last", sesiones.isLast());
            response.put("numberOfElements", sesiones.getNumberOfElements());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * GET /api/sesiones/curso/{idCurso}/activas
     * Obtener sesiones activas de un curso
     */
    @GetMapping("/curso/{idCurso}/activas")
    public ResponseEntity<?> getSesionesActivasPorCurso(@PathVariable Long idCurso) {
        try {
            List<Sesion> sesiones = sesionService.getSesionesActivasPorCurso(idCurso);
            List<SesionActivaResponse> sesionesResponse = sesionService.convertirASesionesActivasResponse(sesiones);
            return ResponseEntity.ok(sesionesResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * GET /api/sesiones/{id}
     * Obtener detalle de una sesión específica
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getSesion(@PathVariable Long id) {
        try {
            Sesion sesion = sesionService.getSesionById(id);
            SesionDetalleResponse sesionResponse = sesionService.convertirASesionDetalleResponse(sesion);
            return ResponseEntity.ok(sesionResponse);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET /api/sesiones/{id}/asistencia
     * Obtener reporte de asistencia de una sesión
     */
    @GetMapping("/{id}/asistencia")
    public ResponseEntity<?> getAsistenciaSesion(@PathVariable Long id) {
        try {
            Sesion sesion = sesionService.getSesionById(id);
            ReporteAsistenciaResponse reporte = sesionService.convertirAReporteAsistenciaResponse(sesion);
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * GET /api/sesiones/{id}/estadisticas
     * Obtener estadísticas de una sesión
     */
    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<?> getEstadisticasSesion(@PathVariable Long id) {
        try {
            Sesion sesion = sesionService.getSesionById(id);
            EstadisticasSesionResponse estadisticas = sesionService.convertirAEstadisticasSesionResponse(sesion);
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * PUT /api/sesiones/{id}
     * Actualizar información de una sesión
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarSesion(
            @PathVariable Long id,
            @RequestBody @Valid ActualizarSesionRequest request) {
        try {
            Sesion sesion = sesionService.actualizarSesion(id, request);
            SesionActualizadaResponse sesionResponse = sesionService.convertirASesionActualizadaResponse(sesion);
            return ResponseEntity.ok(new ApiResponse(true, "Sesión actualizada exitosamente", sesionResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * DELETE /api/sesiones/{id}
     * Eliminar una sesión (solo si no tiene asistencias)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarSesion(@PathVariable Long id) {
        try {
            Sesion sesion = sesionService.getSesionById(id);
            SesionEliminadaResponse sesionResponse = sesionService.convertirASesionEliminadaResponse(sesion);
            sesionService.eliminarSesion(id);
            return ResponseEntity.ok(new ApiResponse(true, "Sesión eliminada exitosamente", sesionResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}