package com.asistencia.backend.controller;

import com.asistencia.backend.dto.*;
import com.asistencia.backend.model.SesionDetalle;
import com.asistencia.backend.response.ApiResponse;
import com.asistencia.backend.service.AsistenciaService;
import com.asistencia.backend.service.SesionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/asistencia")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AsistenciaController {
    
    private final SesionService sesionService;
    private final AsistenciaService asistenciaService;
    
    /**
     * POST /api/asistencia/firmar
     * Estudiante firma su asistencia con geolocalización
     */
    @PostMapping("/firmar")
    public ResponseEntity<?> firmarAsistencia(
            @RequestBody @Valid RegistrarAsistenciaRequest request) {
        try {
            SesionDetalle detalle = sesionService.registrarAsistencia(request);
            AsistenciaRegistradaResponse asistenciaResponse = sesionService.convertirAAsistenciaRegistradaResponse(detalle);
            
            String mensaje = generarMensajeExito(detalle);
            return ResponseEntity.ok(new ApiResponse(true, mensaje, asistenciaResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    private String generarMensajeExito(SesionDetalle detalle) {
        switch (detalle.getEstado()) {
            case PRESENTE:
                return "¡Asistencia registrada exitosamente! Estás presente y en el rango correcto.";
            case TARDIO:
                return "Asistencia registrada como tardío. Llegaste después del tiempo límite pero dentro del rango.";
            case FUERA_RANGO:
                return "Asistencia registrada fuera del rango permitido. Considera acercarte más al aula.";
            default:
                return "Asistencia registrada correctamente";
        }
    }
    
    /**
     * GET /api/asistencia/estudiante/{idEstudiante}
     * Ver historial de asistencia de un estudiante
     */
    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<?> getAsistenciaEstudiante(
            @PathVariable Long idEstudiante,
            @RequestParam(required = false) Long idCurso,
            @RequestParam(required = false) String periodo,
            @PageableDefault(size = 50) Pageable pageable) {
        try {
            Page<SesionDetalleDTO> asistencia = asistenciaService
                .getAsistenciaEstudiante(idEstudiante, idCurso, periodo, pageable);
            return ResponseEntity.ok(asistencia);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * GET /api/asistencia/estudiante/{idEstudiante}/estadisticas
     * Estadísticas de asistencia de un estudiante
     */
    @GetMapping("/estudiante/{idEstudiante}/estadisticas")
    public ResponseEntity<?> getEstadisticasEstudiante(
            @PathVariable Long idEstudiante,
            @RequestParam(required = false) Long idCurso) {
        try {
            EstadisticasEstudianteDTO stats = asistenciaService
                .getEstadisticasEstudiante(idEstudiante, idCurso);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * GET /api/asistencia/validar
     * Validar si un estudiante puede firmar en una sesión
     */
    @GetMapping("/validar")
    public ResponseEntity<?> validarPuedeFirmar(
            @RequestParam Long idSesion,
            @RequestParam Long idEstudiante) {
        try {
            ValidacionAsistenciaResponse validacion = sesionService.validarAsistencia(idSesion, idEstudiante);
            return ResponseEntity.ok(validacion);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * GET /api/asistencia/sesion/{idSesion}/disponible
     * Verificar si una sesión está disponible para firmar
     */
    @GetMapping("/sesion/{idSesion}/disponible")
    public ResponseEntity<?> isSesionDisponible(@PathVariable Long idSesion) {
        try {
            boolean disponible = asistenciaService.isSesionDisponible(idSesion);
            return ResponseEntity.ok(new ApiResponse(
                true, 
                disponible ? "Sesión disponible" : "Sesión no disponible", 
                Map.of("disponible", disponible)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}
