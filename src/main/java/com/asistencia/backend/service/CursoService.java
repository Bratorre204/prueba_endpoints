package com.asistencia.backend.service;

import com.asistencia.backend.dto.*;
import com.asistencia.backend.model.*;
import com.asistencia.backend.repository.*;
import com.asistencia.backend.util.CodigoCursoGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CursoService {
    
    private final CursoRepository cursoRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final UserRepository usuarioRepository;
    private final UsuarioCursoRepository usuarioCursoRepository;
    private final CodigoCursoGenerator codigoCursoGenerator;
    
    public Page<Curso> getAllCursos(String periodo, String turno, Pageable pageable) {
        return cursoRepository.findAllWithFilters(periodo, turno, pageable);
    }
    
    public Curso getCursoById(Long id) {
        return cursoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
    }
    
    public Curso getCursoByCodigo(String codigo) {
        return cursoRepository.findByCodigo(codigo)
            .orElseThrow(() -> new RuntimeException("Curso con código " + codigo + " no encontrado"));
    }
    
    public List<Curso> getCursosPorAñoYSemestre(Integer año, Integer semestre) {
        return cursoRepository.findByAñoAndSemestre(año, semestre);
    }
    
    public List<Curso> getCursosPorTurnoAñoYSemestre(String turno, Integer año, Integer semestre) {
        return cursoRepository.findByTurnoAndAñoAndSemestre(turno, año, semestre);
    }
    
    public List<UsuarioDTO> getEstudiantesPorCurso(Long idCurso) {
        List<UsuarioCurso> inscripciones = usuarioCursoRepository.findByCursoIdAndEstadoActivo(idCurso);
        
        return inscripciones.stream()
            .map(uc -> UsuarioDTO.builder()
                .id(uc.getUsuario().getId())
                .identificacion(uc.getUsuario().getIdentificacion())
                .nombre(uc.getUsuario().getNombre() + " " + uc.getUsuario().getApellido())
                .email(uc.getUsuario().getCorreo())
                .build())
            .collect(Collectors.toList());
    }
    
    public List<Curso> getCursosPorProfesor(Long idProfesor, String periodo) {
        if (periodo != null && !periodo.isEmpty()) {
            return cursoRepository.findByProfesorIdAndPeriodo(idProfesor, periodo);
        }
        return cursoRepository.findByProfesorId(idProfesor);
    }
    
    public List<Curso> getCursosPorEstudiante(Long idEstudiante, String periodo) {
        if (periodo != null && !periodo.isEmpty()) {
            return cursoRepository.findByEstudianteIdAndPeriodo(idEstudiante, periodo);
        }
        return cursoRepository.findByEstudianteId(idEstudiante);
    }
    
    public List<Curso> getCursosPorAsignatura(Long idAsignatura, String periodo) {
        if (periodo != null && !periodo.isEmpty()) {
            return cursoRepository.findByAsignaturaIdAndPeriodo(idAsignatura, periodo);
        }
        return cursoRepository.findByAsignaturaId(idAsignatura);
    }
    
    public Curso crearCurso(CrearCursoRequest request) {
        // Obtener la asignatura
        Asignatura asignatura = asignaturaRepository.findById(request.getIdAsignatura())
            .orElseThrow(() -> new RuntimeException("Asignatura no encontrada"));
        
        // Obtener el profesor
        Usuario profesor = usuarioRepository.findById(request.getIdProfesor())
            .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));
        
        // Generar código del curso
        String codigoCurso = codigoCursoGenerator.generarCodigoCurso(
            asignatura.getCodigo(),
            request.getTurno(),
            request.getAño(),
            request.getSemestre(),
            request.getSeccion()
        );
        
        // Verificar si el código ya existe
        if (cursoRepository.existsByCodigo(codigoCurso)) {
            throw new RuntimeException("Ya existe un curso con el código: " + codigoCurso);
        }
        
        // Generar período
        String periodo = request.getAño() + "-" + request.getSemestre();
        
        Curso curso = Curso.builder()
            .codigo(codigoCurso)
            .descripcion(request.getDescripcion())
            .nombre(request.getNombre())
            .periodo(periodo)
            .turno(request.getTurno())
            .seccion(request.getSeccion())
            .aula(request.getAula())
            .horario(request.getHorario())
            .año(request.getAño())
            .semestre(request.getSemestre())
            .asignatura(asignatura)
            .profesor(profesor)
            .build();
        
        return cursoRepository.save(curso);
    }
    
    public CursoCreadoResponse convertirACursoCreadoResponse(Curso curso) {
        return CursoCreadoResponse.builder()
            .id(curso.getId())
            .codigo(curso.getCodigo())
            .nombre(curso.getNombre())
            .turno(curso.getTurno())
            .seccion(curso.getSeccion())
            .periodo(curso.getPeriodo())
            .asignatura(CursoCreadoResponse.AsignaturaBasica.builder()
                .id(curso.getAsignatura().getId())
                .codigo(curso.getAsignatura().getCodigo())
                .nombre(curso.getAsignatura().getNombre())
                .build())
            .profesor(CursoCreadoResponse.ProfesorBasico.builder()
                .id(curso.getProfesor().getId())
                .identificacion(curso.getProfesor().getIdentificacion())
                .nombre(curso.getProfesor().getNombre())
                .apellidos(curso.getProfesor().getApellido())
                .build())
            .build();
    }
    
    public UsuarioCurso inscribirEstudiante(Long idCurso, InscribirEstudianteRequest request) {
        Curso curso = getCursoById(idCurso);
        
        Usuario estudiante = usuarioRepository.findById(request.getIdEstudiante())
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        
        // Verificar si ya está inscrito
        if (usuarioCursoRepository.existsByUsuarioIdAndCursoId(request.getIdEstudiante(), idCurso)) {
            throw new RuntimeException("El estudiante ya está inscrito en este curso");
        }
        
        UsuarioCurso inscripcion = UsuarioCurso.builder()
            .usuario(estudiante)
            .curso(curso)
            .estado("ACTIVO")
            .observaciones(request.getObservaciones())
            .build();
        
        return usuarioCursoRepository.save(inscripcion);
    }
    
    public List<String> getPeriodosDisponibles() {
        return cursoRepository.findDistinctPeriodos();
    }
    
    public List<String> getTurnosDisponibles() {
        return cursoRepository.findDistinctTurnos();
    }
    
    public List<CursoListaResponse> convertirACursoListaResponse(List<Curso> cursos) {
        return cursos.stream()
            .map(this::convertirACursoListaResponse)
            .collect(Collectors.toList());
    }
    
    public CursoListaResponse convertirACursoListaResponse(Curso curso) {
        return CursoListaResponse.builder()
            .id(curso.getId())
            .codigo(curso.getCodigo())
            .nombre(curso.getNombre())
            .descripcion(curso.getDescripcion())
            .turno(curso.getTurno())
            .seccion(curso.getSeccion())
            .aula(curso.getAula())
            .horario(curso.getHorario())
            .año(curso.getAño())
            .semestre(curso.getSemestre())
            .periodo(curso.getPeriodo())
            .asignatura(CursoListaResponse.AsignaturaBasica.builder()
                .id(curso.getAsignatura().getId())
                .codigo(curso.getAsignatura().getCodigo())
                .nombre(curso.getAsignatura().getNombre())
                .descripcion(curso.getAsignatura().getDescripcion())
                .creditos(curso.getAsignatura().getCreditos())
                .build())
            .profesor(CursoListaResponse.ProfesorBasico.builder()
                .id(curso.getProfesor().getId())
                .identificacion(curso.getProfesor().getIdentificacion())
                .nombre(curso.getProfesor().getNombre())
                .apellidos(curso.getProfesor().getApellido())
                .correo(curso.getProfesor().getCorreo())
                .build())
            .build();
    }
}
