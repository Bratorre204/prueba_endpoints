package com.asistencia.backend.controller;

import com.asistencia.backend.dto.*;
import com.asistencia.backend.model.Curso;
import com.asistencia.backend.model.UsuarioCurso;
import com.asistencia.backend.response.ApiResponse;
import com.asistencia.backend.service.CursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CursoController {
    
    private final CursoService cursoService;
    
    /**
     * GET /api/cursos
     * Listar todos los cursos
     */
    @GetMapping
    public ResponseEntity<?> getAllCursos(
            @RequestParam(required = false) String periodo,
            @RequestParam(required = false) String turno,
            @PageableDefault(size = 50) Pageable pageable) {
        try {
            Page<Curso> cursos = cursoService.getAllCursos(periodo, turno, pageable);
            List<CursoListaResponse> cursosResponse = cursoService.convertirACursoListaResponse(cursos.getContent());
            return ResponseEntity.ok(cursosResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * GET /api/cursos/{id}
     * Obtener detalle de un curso
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCurso(@PathVariable Long id) {
        try {
            Curso curso = cursoService.getCursoById(id);
            CursoListaResponse cursoResponse = cursoService.convertirACursoListaResponse(curso);
            return ResponseEntity.ok(cursoResponse);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET /api/cursos/{id}/estudiantes
     * Listar estudiantes inscritos en un curso
     */
    @GetMapping("/{id}/estudiantes")
    public ResponseEntity<?> getEstudiantesCurso(@PathVariable Long id) {
        try {
            List<UsuarioDTO> estudiantes = cursoService.getEstudiantesPorCurso(id);
            return ResponseEntity.ok(estudiantes);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * GET /api/cursos/profesor/{idProfesor}
     * Cursos asignados a un profesor
     */
    @GetMapping("/profesor/{idProfesor}")
    public ResponseEntity<?> getCursosProfesor(
            @PathVariable Long idProfesor,
            @RequestParam(required = false) String periodo) {
        try {
            List<Curso> cursos = cursoService.getCursosPorProfesor(idProfesor, periodo);
            List<CursoListaResponse> cursosResponse = cursoService.convertirACursoListaResponse(cursos);
            return ResponseEntity.ok(cursosResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * GET /api/cursos/estudiante/{idEstudiante}
     * Cursos en los que está inscrito un estudiante
     */
    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<?> getCursosEstudiante(
            @PathVariable Long idEstudiante,
            @RequestParam(required = false) String periodo) {
        try {
            List<Curso> cursos = cursoService.getCursosPorEstudiante(idEstudiante, periodo);
            List<CursoListaResponse> cursosResponse = cursoService.convertirACursoListaResponse(cursos);
            return ResponseEntity.ok(cursosResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * POST /api/cursos
     * Crear un nuevo curso
     */
    @PostMapping
    public ResponseEntity<?> crearCurso(@RequestBody @Valid CrearCursoRequest request) {
        try {
            Curso curso = cursoService.crearCurso(request);
            CursoCreadoResponse cursoResponse = cursoService.convertirACursoCreadoResponse(curso);
            return ResponseEntity.ok(new ApiResponse(true, "Curso creado exitosamente", cursoResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * POST /api/cursos/{id}/inscribir
     * Inscribir estudiante en un curso
     */
    @PostMapping("/{id}/inscribir")
    public ResponseEntity<?> inscribirEstudiante(
            @PathVariable Long id,
            @RequestBody @Valid InscribirEstudianteRequest request) {
        try {
            UsuarioCurso inscripcion = cursoService.inscribirEstudiante(id, request);
            return ResponseEntity.ok(new ApiResponse(true, "Estudiante inscrito", inscripcion));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * GET /api/cursos/periodos
     * Obtener períodos disponibles
     */
    @GetMapping("/periodos")
    public ResponseEntity<?> getPeriodosDisponibles() {
        try {
            List<String> periodos = cursoService.getPeriodosDisponibles();
            return ResponseEntity.ok(periodos);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * GET /api/cursos/asignatura/{idAsignatura}
     * Cursos de una asignatura específica
     */
    @GetMapping("/asignatura/{idAsignatura}")
    public ResponseEntity<?> getCursosPorAsignatura(
            @PathVariable Long idAsignatura,
            @RequestParam(required = false) String periodo) {
        try {
            List<Curso> cursos = cursoService.getCursosPorAsignatura(idAsignatura, periodo);
            List<CursoListaResponse> cursosResponse = cursoService.convertirACursoListaResponse(cursos);
            return ResponseEntity.ok(cursosResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}
