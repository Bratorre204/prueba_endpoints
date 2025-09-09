package com.asistencia.backend.controller;

import com.asistencia.backend.dto.CursoRequest;
import com.asistencia.backend.dto.CursoResponse;
import com.asistencia.backend.response.ApiResponse;
import com.asistencia.backend.service.CursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @PostMapping
    public ResponseEntity<ApiResponse<CursoResponse>> crearCurso(
            @Valid @RequestBody CursoRequest request,
            Authentication authentication) {
        try {
            CursoResponse curso = cursoService.crearCurso(request, authentication.getName());

            ApiResponse<CursoResponse> response = ApiResponse.<CursoResponse>builder()
                    .status(true)
                    .message("Curso creado exitosamente")
                    .data(curso)
                    .build();

            return ResponseEntity.status(201).body(response);

        } catch (Exception e) {
            ApiResponse<CursoResponse> errorResponse = ApiResponse.<CursoResponse>builder()
                    .status(false)
                    .message("Error al crear curso: " + e.getMessage())
                    .data(null)
                    .build();

            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @GetMapping("/mis-cursos")
    public ResponseEntity<ApiResponse<List<CursoResponse>>> obtenerMisCursos(Authentication authentication) {
        try {
            List<CursoResponse> cursos = cursoService.obtenerCursosPorProfesor(authentication.getName());

            ApiResponse<List<CursoResponse>> response = ApiResponse.<List<CursoResponse>>builder()
                    .status(true)
                    .message("Cursos obtenidos exitosamente")
                    .data(cursos)
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<List<CursoResponse>> errorResponse = ApiResponse.<List<CursoResponse>>builder()
                    .status(false)
                    .message("Error: " + e.getMessage())
                    .data(null)
                    .build();

            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CursoResponse>>> obtenerTodosCursos(
            @RequestParam(required = false) String buscar) {
        try {
            List<CursoResponse> cursos;

            cursos = cursoService.obtenerTodosCursosActivos();

            ApiResponse<List<CursoResponse>> response = ApiResponse.<List<CursoResponse>>builder()
                    .status(true)
                    .message("Cursos obtenidos exitosamente")
                    .data(cursos)
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<List<CursoResponse>> errorResponse = ApiResponse.<List<CursoResponse>>builder()
                    .status(false)
                    .message("Error: " + e.getMessage())
                    .data(null)
                    .build();

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CursoResponse>> obtenerCurso(@PathVariable Long id) {
        try {
            CursoResponse curso = cursoService.obtenerCursoPorId(id);

            ApiResponse<CursoResponse> response = ApiResponse.<CursoResponse>builder()
                    .status(true)
                    .message("Curso encontrado")
                    .data(curso)
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<CursoResponse> errorResponse = ApiResponse.<CursoResponse>builder()
                    .status(false)
                    .message("Error: " + e.getMessage())
                    .data(null)
                    .build();

            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CursoResponse>> actualizarCurso(
            @PathVariable Long id,
            @Valid @RequestBody CursoRequest request,
            Authentication authentication) {
        try {
            CursoResponse curso = cursoService.actualizarCurso(id, request, authentication.getName());

            ApiResponse<CursoResponse> response = ApiResponse.<CursoResponse>builder()
                    .status(true)
                    .message("Curso actualizado exitosamente")
                    .data(curso)
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<CursoResponse> errorResponse = ApiResponse.<CursoResponse>builder()
                    .status(false)
                    .message("Error: " + e.getMessage())
                    .data(null)
                    .build();

            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarCurso(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            cursoService.eliminarCurso(id, authentication.getName());

            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .status(true)
                    .message("Curso eliminado exitosamente")
                    .data(null)
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<Void> errorResponse = ApiResponse.<Void>builder()
                    .status(false)
                    .message("Error: " + e.getMessage())
                    .data(null)
                    .build();

            return ResponseEntity.status(400).body(errorResponse);
        }

    }
}