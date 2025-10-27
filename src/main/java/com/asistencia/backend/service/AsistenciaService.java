package com.asistencia.backend.service;

import com.asistencia.backend.dto.*;
import com.asistencia.backend.model.*;
import com.asistencia.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AsistenciaService {
    
    private final SesionDetalleRepository sesionDetalleRepository;
    private final SesionRepository sesionRepository;
    private final UsuarioCursoRepository usuarioCursoRepository;
    
    public Page<SesionDetalleDTO> getAsistenciaEstudiante(Long idEstudiante, Long idCurso, String periodo, Pageable pageable) {
        Page<SesionDetalle> detalles;
        
        if (idCurso != null) {
            detalles = sesionDetalleRepository.findByEstudianteIdAndCursoId(idEstudiante, idCurso, pageable);
        } else if (periodo != null && !periodo.isEmpty()) {
            detalles = sesionDetalleRepository.findByEstudianteIdAndPeriodo(idEstudiante, periodo, pageable);
        } else {
            detalles = sesionDetalleRepository.findByEstudianteId(idEstudiante, pageable);
        }
        
        return detalles.map(this::convertirADTO);
    }
    
    public EstadisticasEstudianteDTO getEstadisticasEstudiante(Long idEstudiante, Long idCurso) {
        Long totalSesiones;
        Long asistencias;
        Long ausencias;
        Long tardias;
        Long fueraRango;
        String curso = null;
        
        if (idCurso != null) {
            totalSesiones = sesionRepository.countByCursoId(idCurso);
            asistencias = sesionDetalleRepository.countByEstudianteIdAndCursoIdAndEstado(idEstudiante, idCurso, EstadoAsistencia.PRESENTE);
            ausencias = sesionDetalleRepository.countByEstudianteIdAndCursoIdAndEstado(idEstudiante, idCurso, EstadoAsistencia.AUSENTE);
            tardias = sesionDetalleRepository.countByEstudianteIdAndCursoIdAndEstado(idEstudiante, idCurso, EstadoAsistencia.TARDIO);
            fueraRango = sesionDetalleRepository.countByEstudianteIdAndCursoIdAndEstado(idEstudiante, idCurso, EstadoAsistencia.FUERA_RANGO);
        } else {
            totalSesiones = sesionDetalleRepository.countByEstudianteIdAndEstado(idEstudiante, EstadoAsistencia.PRESENTE) +
                           sesionDetalleRepository.countByEstudianteIdAndEstado(idEstudiante, EstadoAsistencia.AUSENTE) +
                           sesionDetalleRepository.countByEstudianteIdAndEstado(idEstudiante, EstadoAsistencia.TARDIO) +
                           sesionDetalleRepository.countByEstudianteIdAndEstado(idEstudiante, EstadoAsistencia.FUERA_RANGO);
            asistencias = sesionDetalleRepository.countByEstudianteIdAndEstado(idEstudiante, EstadoAsistencia.PRESENTE);
            ausencias = sesionDetalleRepository.countByEstudianteIdAndEstado(idEstudiante, EstadoAsistencia.AUSENTE);
            tardias = sesionDetalleRepository.countByEstudianteIdAndEstado(idEstudiante, EstadoAsistencia.TARDIO);
            fueraRango = sesionDetalleRepository.countByEstudianteIdAndEstado(idEstudiante, EstadoAsistencia.FUERA_RANGO);
        }
        
        Double porcentajeAsistencia = totalSesiones > 0 ? 
            (double) asistencias / totalSesiones * 100 : 0.0;
        
        return EstadisticasEstudianteDTO.builder()
            .totalSesiones(totalSesiones)
            .asistencias(asistencias)
            .ausencias(ausencias)
            .tardias(tardias)
            .fueraRango(fueraRango)
            .porcentajeAsistencia(porcentajeAsistencia)
            .curso(curso)
            .build();
    }
    
    public ValidacionFirmaDTO validarPuedeFirmar(Long idSesion, Long idEstudiante) {
        Sesion sesion = sesionRepository.findById(idSesion)
            .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));
        
        // Verificar si la sesión está abierta
        if (!"ABIERTA".equals(sesion.getEstado())) {
            return ValidacionFirmaDTO.builder()
                .puedeFirmar(false)
                .mensaje("La sesión no está abierta para firmar")
                .sesion(sesion)
                .build();
        }
        
        // Verificar si el estudiante está inscrito
        UsuarioCurso inscripcion = usuarioCursoRepository.findByUsuarioIdAndCursoId(idEstudiante, sesion.getCurso().getId());
        if (inscripcion == null) {
            return ValidacionFirmaDTO.builder()
                .puedeFirmar(false)
                .mensaje("El estudiante no está inscrito en este curso")
                .sesion(sesion)
                .build();
        }
        
        // Verificar si ya firmó
        SesionDetalle detalleExistente = sesionDetalleRepository.findBySesionIdAndEstudianteId(idSesion, idEstudiante);
        if (detalleExistente != null) {
            return ValidacionFirmaDTO.builder()
                .puedeFirmar(false)
                .mensaje("El estudiante ya firmó en esta sesión")
                .sesion(sesion)
                .build();
        }
        
        return ValidacionFirmaDTO.builder()
            .puedeFirmar(true)
            .mensaje("El estudiante puede firmar")
            .sesion(sesion)
            .build();
    }
    
    public boolean isSesionDisponible(Long idSesion) {
        Sesion sesion = sesionRepository.findById(idSesion)
            .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));
        
        return "ABIERTA".equals(sesion.getEstado());
    }
    
    private SesionDetalleDTO convertirADTO(SesionDetalle detalle) {
        return SesionDetalleDTO.builder()
            .id(detalle.getId())
            .fechaFirma(detalle.getFechaFirma())
            .estudiante(detalle.getEstudiante().getNombre() + " " + detalle.getEstudiante().getApellido())
            .curso(detalle.getSesion().getCurso().getNombre())
            .estado(detalle.getEstado())
            .distanciaMetros(detalle.getDistanciaMetros())
            .observacion(detalle.getObservacion())
            .build();
    }
}
