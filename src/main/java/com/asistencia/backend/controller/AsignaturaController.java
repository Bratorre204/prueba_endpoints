package com.asistencia.backend.controller;

import com.asistencia.backend.dto.CrearAsignaturaRequest;
import com.asistencia.backend.model.Asignatura;
import com.asistencia.backend.response.ApiResponse;
import com.asistencia.backend.service.AsignaturaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asignaturas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AsignaturaController {
    
    private final AsignaturaService asignaturaService;
    
    /**
     * GET /api/asignaturas
     * Listar todas las asignaturas
     */
    @GetMapping
    public ResponseEntity<?> getAllAsignaturas(@PageableDefault(size = 50) Pageable pageable) {
        try {
            Page<Asignatura> asignaturas = asignaturaService.getAllAsignaturas(pageable);
            return ResponseEntity.ok(asignaturas);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * GET /api/asignaturas/{id}
     * Obtener detalle de una asignatura
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAsignatura(@PathVariable Long id) {
        try {
            Asignatura asignatura = asignaturaService.getAsignaturaById(id);
            return ResponseEntity.ok(asignatura);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET /api/asignaturas/codigo/{codigo}
     * Obtener asignatura por código
     */
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<?> getAsignaturaByCodigo(@PathVariable String codigo) {
        try {
            Asignatura asignatura = asignaturaService.getAsignaturaByCodigo(codigo);
            return ResponseEntity.ok(asignatura);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET /api/asignaturas/buscar
     * Buscar asignaturas por nombre, código o descripción
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarAsignaturas(@RequestParam String query) {
        try {
            List<Asignatura> asignaturas = asignaturaService.buscarAsignaturas(query);
            return ResponseEntity.ok(asignaturas);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * POST /api/asignaturas
     * Crear una nueva asignatura
     */
    @PostMapping
    public ResponseEntity<?> crearAsignatura(@RequestBody @Valid CrearAsignaturaRequest request) {
        try {
            Asignatura asignatura = asignaturaService.crearAsignatura(request);
            return ResponseEntity.ok(new ApiResponse(true, "Asignatura creada exitosamente", asignatura));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * PUT /api/asignaturas/{id}
     * Actualizar una asignatura
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarAsignatura(
            @PathVariable Long id,
            @RequestBody @Valid CrearAsignaturaRequest request) {
        try {
            Asignatura asignatura = asignaturaService.actualizarAsignatura(id, request);
            return ResponseEntity.ok(new ApiResponse(true, "Asignatura actualizada", asignatura));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * DELETE /api/asignaturas/{id}
     * Eliminar una asignatura
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarAsignatura(@PathVariable Long id) {
        try {
            asignaturaService.eliminarAsignatura(id);
            return ResponseEntity.ok(new ApiResponse(true, "Asignatura eliminada", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}