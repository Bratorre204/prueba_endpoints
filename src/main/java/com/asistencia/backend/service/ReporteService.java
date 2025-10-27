package com.asistencia.backend.service;

import com.asistencia.backend.dto.*;
import com.asistencia.backend.model.*;
import com.asistencia.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteService {
    
    private final SesionRepository sesionRepository;
    private final SesionDetalleRepository sesionDetalleRepository;
    private final UsuarioCursoRepository usuarioCursoRepository;
    private final CursoRepository cursoRepository;
    
    public ReporteAsistenciaCursoDTO getReporteAsistenciaCurso(Long idCurso, LocalDate fechaInicio, LocalDate fechaFin) {
        Curso curso = cursoRepository.findById(idCurso)
            .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        
        List<Sesion> sesiones = sesionRepository.findByCursoId(idCurso);
        
        // Filtrar por fechas si se proporcionan
        if (fechaInicio != null && fechaFin != null) {
            LocalDateTime inicio = fechaInicio.atStartOfDay();
            LocalDateTime fin = fechaFin.atTime(23, 59, 59);
            sesiones = sesiones.stream()
                .filter(s -> s.getFechaCreacion().isAfter(inicio) && s.getFechaCreacion().isBefore(fin))
                .collect(Collectors.toList());
        }
        
        final List<Sesion> sesionesFinal = sesiones; // Hacer la variable final para usar en lambda
        
        List<UsuarioCurso> estudiantes = usuarioCursoRepository.findByCursoIdAndEstadoActivo(idCurso);
        
        List<EstudianteAsistenciaDTO> estudiantesAsistencia = estudiantes.stream()
            .map(uc -> {
                Long asistencias = sesionDetalleRepository.countByEstudianteIdAndCursoIdAndEstado(
                    uc.getUsuario().getId(), idCurso, EstadoAsistencia.PRESENTE);
                Long ausencias = sesionesFinal.size() - asistencias;
                Double porcentaje = sesionesFinal.size() > 0 ? (double) asistencias / sesionesFinal.size() * 100 : 0.0;
                
                return EstudianteAsistenciaDTO.builder()
                    .nombre(uc.getUsuario().getNombre() + " " + uc.getUsuario().getApellido())
                    .identificacion(uc.getUsuario().getCorreo())
                    .asistencias(asistencias.intValue())
                    .ausencias(ausencias.intValue())
                    .porcentaje(porcentaje)
                    .build();
            })
            .collect(Collectors.toList());
        
        Double promedioAsistencia = estudiantesAsistencia.stream()
            .mapToDouble(EstudianteAsistenciaDTO::getPorcentaje)
            .average()
            .orElse(0.0);
        
        EstadisticasGeneralesDTO estadisticas = EstadisticasGeneralesDTO.builder()
            .totalSesiones(sesionesFinal.size())
            .promedioAsistencia(promedioAsistencia)
            .build();
        
        return ReporteAsistenciaCursoDTO.builder()
            .curso(curso.getNombre())
            .periodo(curso.getPeriodo())
            .estudiantes(estudiantesAsistencia)
            .estadisticas(estadisticas)
            .build();
    }
    
    public ReporteConsolidadoEstudianteDTO getReporteConsolidadoEstudiante(Long idEstudiante, String periodo) {
        List<Curso> cursos = cursoRepository.findByEstudianteId(idEstudiante);
        
        if (periodo != null && !periodo.isEmpty()) {
            cursos = cursos.stream()
                .filter(c -> periodo.equals(c.getPeriodo()))
                .collect(Collectors.toList());
        }
        
        List<AsistenciaPorCursoDTO> asistenciaPorCurso = cursos.stream()
            .map(curso -> {
                Long asistencias = sesionDetalleRepository.countByEstudianteIdAndCursoIdAndEstado(
                    idEstudiante, curso.getId(), EstadoAsistencia.PRESENTE);
                Long totalSesiones = sesionRepository.countByCursoId(curso.getId());
                Long ausencias = totalSesiones - asistencias;
                Double porcentaje = totalSesiones > 0 ? (double) asistencias / totalSesiones * 100 : 0.0;
                
                return AsistenciaPorCursoDTO.builder()
                    .curso(curso.getNombre())
                    .asistencias(asistencias.intValue())
                    .ausencias(ausencias.intValue())
                    .porcentaje(porcentaje)
                    .build();
            })
            .collect(Collectors.toList());
        
        return ReporteConsolidadoEstudianteDTO.builder()
            .estudiante("Estudiante") // Se puede obtener el nombre del usuario
            .cursos(asistenciaPorCurso)
            .build();
    }
    
    public ReporteConsolidadoProfesorDTO getReporteConsolidadoProfesor(Long idProfesor, String periodo) {
        List<Curso> cursos = cursoRepository.findByProfesorId(idProfesor);
        
        if (periodo != null && !periodo.isEmpty()) {
            cursos = cursos.stream()
                .filter(c -> periodo.equals(c.getPeriodo()))
                .collect(Collectors.toList());
        }
        
        List<CursoEstadisticasDTO> cursoEstadisticas = cursos.stream()
            .map(curso -> {
                Integer sesionesRealizadas = sesionRepository.countByCursoId(curso.getId()).intValue();
                
                List<SesionDetalle> detalles = sesionDetalleRepository.findBySesionId(curso.getId());
                Double promedioAsistencia = detalles.stream()
                    .filter(d -> d.getEstado() == EstadoAsistencia.PRESENTE)
                    .count() / (double) detalles.size() * 100;
                
                return CursoEstadisticasDTO.builder()
                    .curso(curso.getNombre())
                    .sesionesRealizadas(sesionesRealizadas)
                    .promedioAsistencia(promedioAsistencia)
                    .build();
            })
            .collect(Collectors.toList());
        
        return ReporteConsolidadoProfesorDTO.builder()
            .profesor("Profesor") // Se puede obtener el nombre del usuario
            .cursos(cursoEstadisticas)
            .build();
    }
    
    public byte[] exportarReporteExcel(Long idCurso) {
        // Implementación básica - en un proyecto real usarías Apache POI
        // Por ahora retornamos un array vacío
        return new byte[0];
    }
    
    public DashboardDTO getDashboard(String periodo) {
        LocalDateTime hoy = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        
        Integer totalSesionesHoy = sesionRepository.findActivasDesdeFecha(hoy).size();
        Integer totalEstudiantesActivos = usuarioCursoRepository.findByUsuarioIdAndEstadoActivo(1L).size(); // Simplificado
        
        // Calcular promedio general de asistencia
        List<SesionDetalle> todasLasAsistencias = sesionDetalleRepository.findAll();
        Double promedioAsistenciaGeneral = todasLasAsistencias.stream()
            .filter(d -> d.getEstado() == EstadoAsistencia.PRESENTE)
            .count() / (double) todasLasAsistencias.size() * 100;
        
        // Cursos más activos (simplificado)
        List<CursoMasActivoDTO> cursosMasActivos = cursoRepository.findAll().stream()
            .limit(5)
            .map(curso -> {
                Integer sesiones = sesionRepository.countByCursoId(curso.getId()).intValue();
                Double asistencia = 85.0; // Valor simplificado
                
                return CursoMasActivoDTO.builder()
                    .curso(curso.getNombre())
                    .sesiones(sesiones)
                    .asistencia(asistencia)
                    .build();
            })
            .collect(Collectors.toList());
        
        return DashboardDTO.builder()
            .totalSesionesHoy(totalSesionesHoy)
            .totalEstudiantesActivos(totalEstudiantesActivos)
            .promedioAsistenciaGeneral(promedioAsistenciaGeneral)
            .cursosMasActivos(cursosMasActivos)
            .build();
    }
}
