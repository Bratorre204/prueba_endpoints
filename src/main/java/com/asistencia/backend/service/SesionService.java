package com.asistencia.backend.service;

import com.asistencia.backend.dto.*;
import com.asistencia.backend.model.*;
import com.asistencia.backend.repository.*;
import com.asistencia.backend.util.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SesionService {

    private final SesionRepository sesionRepository;
    private final SesionDetalleRepository sesionDetalleRepository;
    private final UserRepository usuarioRepository;
    private final CursoRepository cursoRepository;
    private final UsuarioCursoRepository usuarioCursoRepository;
    
    public Sesion crearSesion(CrearSesionRequest request) {
        Curso curso = cursoRepository.findById(request.getIdCurso())
            .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        
        LocalDateTime fechaInicio = LocalDateTime.now();
        
        // Validar que no exista una sesión activa para el mismo curso en el mismo día
        Long sesionesActivasHoy = sesionRepository.countActivasByCursoIdAndFecha(request.getIdCurso(), fechaInicio);
        if (sesionesActivasHoy > 0) {
            throw new RuntimeException("Ya existe una sesión activa para este curso hoy. Debe cerrar la sesión actual antes de crear una nueva.");
        }
        
        // Validar horario según el turno del curso
        validarHorarioSegunTurno(curso.getTurno(), fechaInicio);
        
        Sesion sesion = Sesion.builder()
            .curso(curso)
            .profesor(curso.getProfesor()) // El profesor se obtiene del curso
            .nombre(request.getNombre())
            .descripcion(request.getDescripcion())
            .aula(request.getAula())
            .latitudProfesor(request.getLatitudProfesor())
            .longitudProfesor(request.getLongitudProfesor())
            .radioProximidad(6.0) // Siempre 6 metros
            .fechaInicio(fechaInicio) // Fecha de inicio automática
            .fechaFin(request.getFechaFin()) // Fecha de fin opcional
            .estado("ACTIVA") // Siempre ACTIVA al crear
            .build();
        
        return sesionRepository.save(sesion);
    }
    
    private void validarHorarioSegunTurno(String turno, LocalDateTime fechaInicio) {
        int hora = fechaInicio.getHour();
        
        if ("DIURNA".equalsIgnoreCase(turno)) {
            // Sesiones diurnas: entre 6:00 AM y 6:00 PM
            if (hora < 6 || hora >= 18) {
                throw new RuntimeException("Las sesiones diurnas solo pueden crearse entre las 6:00 AM y 6:00 PM. Hora actual: " + fechaInicio.getHour() + ":00");
            }
        } else if ("NOCTURNA".equalsIgnoreCase(turno)) {
            // Sesiones nocturnas: entre 6:00 PM y 6:00 AM del día siguiente
            if (hora >= 6 && hora < 18) {
                throw new RuntimeException("Las sesiones nocturnas solo pueden crearse entre las 6:00 PM y 6:00 AM. Hora actual: " + fechaInicio.getHour() + ":00");
            }
        }
    }

    public SesionCreadaResponse convertirASesionCreadaResponse(Sesion sesion) {
        return SesionCreadaResponse.builder()
            .id(sesion.getId())
            .nombre(sesion.getNombre())
            .descripcion(sesion.getDescripcion())
            .aula(sesion.getAula())
            .estado(sesion.getEstado())
            .radioProximidad(sesion.getRadioProximidad())
            .fechaInicio(sesion.getFechaInicio())
            .fechaFin(sesion.getFechaFin())
            .curso(SesionCreadaResponse.CursoInfo.builder()
                .id(sesion.getCurso().getId())
                .codigo(sesion.getCurso().getCodigo())
                .nombre(sesion.getCurso().getNombre())
                .turno(sesion.getCurso().getTurno())
                .seccion(sesion.getCurso().getSeccion())
                .periodo(sesion.getCurso().getPeriodo())
                .build())
            .profesor(SesionCreadaResponse.ProfesorInfo.builder()
                .id(sesion.getProfesor().getId())
                .identificacion(sesion.getProfesor().getIdentificacion())
                .nombre(sesion.getProfesor().getNombre())
                .apellidos(sesion.getProfesor().getApellido())
                .correo(sesion.getProfesor().getCorreo())
                .build())
            .build();
    }
    
    public Sesion cerrarSesion(Long id) {
        Sesion sesion = sesionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));
        
        // Validaciones para cerrar sesión
        validarCierreSesion(sesion);
        
        sesion.setEstado("FINALIZADA");
        sesion.setFechaCierre(LocalDateTime.now());
        
        return sesionRepository.save(sesion);
    }
    
    private void validarCierreSesion(Sesion sesion) {
        // Validar que la sesión esté activa
        if (!"ACTIVA".equals(sesion.getEstado())) {
            throw new RuntimeException("Solo se pueden cerrar sesiones que estén en estado ACTIVA. Estado actual: " + sesion.getEstado());
        }
        
        // Validar que la sesión tenga fecha de inicio
        if (sesion.getFechaInicio() == null) {
            throw new RuntimeException("No se puede cerrar una sesión que no tiene fecha de inicio");
        }
        
        // Validar que no se esté cerrando antes de tiempo (opcional)
        LocalDateTime ahora = LocalDateTime.now();
        if (sesion.getFechaFin() != null && ahora.isBefore(sesion.getFechaFin())) {
            // Solo mostrar advertencia, no bloquear
            System.out.println("ADVERTENCIA: Cerrando sesión antes de la fecha de fin programada");
        }
        
        // Validar que haya pasado al menos 1 minuto desde la creación
        long minutosTranscurridos = java.time.Duration.between(sesion.getFechaInicio(), ahora).toMinutes();
        if (minutosTranscurridos < 1) {
            throw new RuntimeException("No se puede cerrar una sesión antes de 1 minuto de haberla creado");
        }
    }
    
    public SesionCerradaResponse convertirASesionCerradaResponse(Sesion sesion) {
        // Obtener estadísticas finales
        SesionCerradaResponse.EstadisticasFinales estadisticas = obtenerEstadisticasFinales(sesion);
        
        // Obtener resumen de asistencia
        SesionCerradaResponse.ResumenAsistencia resumenAsistencia = obtenerResumenAsistencia(sesion.getId());
        
        return SesionCerradaResponse.builder()
            .id(sesion.getId())
            .nombre(sesion.getNombre())
            .descripcion(sesion.getDescripcion())
            .aula(sesion.getAula())
            .estado(sesion.getEstado())
            .radioProximidad(sesion.getRadioProximidad())
            .fechaInicio(sesion.getFechaInicio())
            .fechaFin(sesion.getFechaFin())
            .fechaCierre(sesion.getFechaCierre())
            .curso(SesionCerradaResponse.ResumenCurso.builder()
                .id(sesion.getCurso().getId())
                .codigo(sesion.getCurso().getCodigo())
                .nombre(sesion.getCurso().getNombre())
                .turno(sesion.getCurso().getTurno())
                .seccion(sesion.getCurso().getSeccion())
                .periodo(sesion.getCurso().getPeriodo())
                .build())
            .profesor(SesionCerradaResponse.ResumenProfesor.builder()
                .id(sesion.getProfesor().getId())
                .identificacion(sesion.getProfesor().getIdentificacion())
                .nombre(sesion.getProfesor().getNombre())
                .apellidos(sesion.getProfesor().getApellido())
                .build())
            .estadisticas(estadisticas)
            .resumenAsistencia(resumenAsistencia)
            .build();
    }
    
    private SesionCerradaResponse.EstadisticasFinales obtenerEstadisticasFinales(Sesion sesion) {
        try {
            EstadisticasSesionDTO stats = getEstadisticasSesion(sesion.getId());
            
            // Calcular duración en minutos
            LocalDateTime inicio = sesion.getFechaInicio();
            LocalDateTime fin = sesion.getFechaCierre() != null ? sesion.getFechaCierre() : LocalDateTime.now();
            Long duracionMinutos = java.time.Duration.between(inicio, fin).toMinutes();
            
            // Calcular porcentaje que firmaron
            Double porcentajeFirmaron = stats.getTotalInscritos() > 0 ? 
                (double) stats.getTotalFirmaron() / stats.getTotalInscritos() * 100 : 0.0;
            
            // Determinar estado final
            String estadoFinal = determinarEstadoFinal(stats);
            
            return SesionCerradaResponse.EstadisticasFinales.builder()
                .totalInscritos(stats.getTotalInscritos())
                .totalFirmaron(stats.getTotalFirmaron())
                .presentes(stats.getPresentes())
                .ausentes(stats.getAusentes())
                .tardios(stats.getTardios())
                .fueraRango(stats.getFueraRango())
                .porcentajeAsistencia(stats.getPorcentajeAsistencia())
                .porcentajeFirmaron(porcentajeFirmaron)
                .duracionMinutos(duracionMinutos)
                .estadoFinal(estadoFinal)
                .build();
        } catch (Exception e) {
            return SesionCerradaResponse.EstadisticasFinales.builder()
                .totalInscritos(0L)
                .totalFirmaron(0L)
                .presentes(0L)
                .ausentes(0L)
                .tardios(0L)
                .fueraRango(0L)
                .porcentajeAsistencia(0.0)
                .porcentajeFirmaron(0.0)
                .duracionMinutos(0L)
                .estadoFinal("ERROR")
                .build();
        }
    }
    
    private String determinarEstadoFinal(EstadisticasSesionDTO stats) {
        if (stats.getPorcentajeAsistencia() >= 80) {
            return "EXCELENTE";
        } else if (stats.getPorcentajeAsistencia() >= 60) {
            return "BUENA";
        } else if (stats.getPorcentajeAsistencia() >= 40) {
            return "REGULAR";
        } else {
            return "BAJA";
        }
    }
    
    private SesionCerradaResponse.ResumenAsistencia obtenerResumenAsistencia(Long idSesion) {
        try {
            List<SesionDetalleDTO> detalles = obtenerAsistenciaSesion(idSesion);
            
            Long estudiantesEnRango = detalles.stream()
                .filter(d -> d.getDistanciaMetros() <= 6.0)
                .count();
            
            Long estudiantesFueraRango = detalles.stream()
                .filter(d -> d.getDistanciaMetros() > 6.0)
                .count();
            
            Double promedioDistancia = detalles.stream()
                .mapToDouble(SesionDetalleDTO::getDistanciaMetros)
                .average()
                .orElse(0.0);
            
            // Generar mensaje resumen
            String mensajeResumen = generarMensajeResumen(estudiantesEnRango, estudiantesFueraRango, promedioDistancia);
            
            // Determinar si la asistencia fue exitosa
            Boolean asistenciaExitosa = estudiantesEnRango > estudiantesFueraRango;
            
            return SesionCerradaResponse.ResumenAsistencia.builder()
                .estudiantesEnRango(estudiantesEnRango)
                .estudiantesFueraRango(estudiantesFueraRango)
                .promedioDistancia(promedioDistancia)
                .mensajeResumen(mensajeResumen)
                .asistenciaExitosa(asistenciaExitosa)
                .build();
        } catch (Exception e) {
            return SesionCerradaResponse.ResumenAsistencia.builder()
                .estudiantesEnRango(0L)
                .estudiantesFueraRango(0L)
                .promedioDistancia(0.0)
                .mensajeResumen("Error al obtener resumen de asistencia")
                .asistenciaExitosa(false)
                .build();
        }
    }
    
    private String generarMensajeResumen(Long enRango, Long fueraRango, Double promedioDistancia) {
        if (enRango > fueraRango) {
            return String.format("Excelente asistencia: %d estudiantes en rango, %d fuera de rango. Distancia promedio: %.1f metros", 
                enRango, fueraRango, promedioDistancia);
        } else if (enRango.equals(fueraRango)) {
            return String.format("Asistencia equilibrada: %d estudiantes en rango, %d fuera de rango. Distancia promedio: %.1f metros", 
                enRango, fueraRango, promedioDistancia);
        } else {
            return String.format("Asistencia con problemas: %d estudiantes en rango, %d fuera de rango. Distancia promedio: %.1f metros", 
                enRango, fueraRango, promedioDistancia);
        }
    }

    public Page<Sesion> getSesionesPorProfesor(Long idProfesor, String periodo, Pageable pageable) {
        if (periodo != null && !periodo.isEmpty()) {
            return sesionRepository.findByProfesorIdAndPeriodo(idProfesor, periodo, pageable);
        }
        return sesionRepository.findByProfesorId(idProfesor, pageable);
    }
    
    public List<SesionProfesorResponse> convertirASesionesProfesorResponse(List<Sesion> sesiones) {
        return sesiones.stream()
            .map(this::convertirASesionProfesorResponse)
            .collect(Collectors.toList());
    }
    
    public SesionProfesorResponse convertirASesionProfesorResponse(Sesion sesion) {
        // Obtener estadísticas básicas para la sesión
        SesionProfesorResponse.EstadisticasBasicas estadisticas = obtenerEstadisticasBasicas(sesion.getId());
        
        return SesionProfesorResponse.builder()
            .id(sesion.getId())
            .nombre(sesion.getNombre())
            .descripcion(sesion.getDescripcion())
            .aula(sesion.getAula())
            .estado(sesion.getEstado())
            .radioProximidad(sesion.getRadioProximidad())
            .fechaInicio(sesion.getFechaInicio())
            .fechaFin(sesion.getFechaFin())
            .fechaCierre(sesion.getFechaCierre())
            .curso(SesionProfesorResponse.CursoInfo.builder()
                .id(sesion.getCurso().getId())
                .codigo(sesion.getCurso().getCodigo())
                .nombre(sesion.getCurso().getNombre())
                .turno(sesion.getCurso().getTurno())
                .seccion(sesion.getCurso().getSeccion())
                .periodo(sesion.getCurso().getPeriodo())
                .aula(sesion.getCurso().getAula())
                .horario(sesion.getCurso().getHorario())
                .build())
            .estadisticas(estadisticas)
            .build();
    }
    
    private SesionProfesorResponse.EstadisticasBasicas obtenerEstadisticasBasicas(Long idSesion) {
        try {
            EstadisticasSesionDTO stats = getEstadisticasSesion(idSesion);
            return SesionProfesorResponse.EstadisticasBasicas.builder()
                .totalInscritos(stats.getTotalInscritos())
                .totalFirmaron(stats.getTotalFirmaron())
                .presentes(stats.getPresentes())
                .ausentes(stats.getAusentes())
                .tardios(stats.getTardios())
                .fueraRango(stats.getFueraRango())
                .porcentajeAsistencia(stats.getPorcentajeAsistencia())
                .build();
        } catch (Exception e) {
            // Si hay error, devolver estadísticas vacías
            return SesionProfesorResponse.EstadisticasBasicas.builder()
                .totalInscritos(0L)
                .totalFirmaron(0L)
                .presentes(0L)
                .ausentes(0L)
                .tardios(0L)
                .fueraRango(0L)
                .porcentajeAsistencia(0.0)
                .build();
        }
    }
    
    public List<Sesion> getSesionesActivasPorCurso(Long idCurso) {
        return sesionRepository.findActivasByCursoId(idCurso);
    }
    
    public List<SesionActivaResponse> convertirASesionesActivasResponse(List<Sesion> sesiones) {
        return sesiones.stream()
            .map(this::convertirASesionActivaResponse)
            .collect(Collectors.toList());
    }
    
    public SesionActivaResponse convertirASesionActivaResponse(Sesion sesion) {
        return SesionActivaResponse.builder()
            .id(sesion.getId())
            .nombre(sesion.getNombre())
            .descripcion(sesion.getDescripcion())
            .aula(sesion.getAula())
            .estado(sesion.getEstado())
            .radioProximidad(sesion.getRadioProximidad())
            .fechaInicio(sesion.getFechaInicio())
            .fechaFin(sesion.getFechaFin())
            .profesor(SesionActivaResponse.ProfesorBasico.builder()
                .id(sesion.getProfesor().getId())
                .identificacion(sesion.getProfesor().getIdentificacion())
                .nombre(sesion.getProfesor().getNombre())
                .apellidos(sesion.getProfesor().getApellido())
                .build())
            .curso(SesionActivaResponse.CursoBasico.builder()
                .id(sesion.getCurso().getId())
                .codigo(sesion.getCurso().getCodigo())
                .nombre(sesion.getCurso().getNombre())
                .turno(sesion.getCurso().getTurno())
                .seccion(sesion.getCurso().getSeccion())
                .periodo(sesion.getCurso().getPeriodo())
                .build())
            .build();
    }
    
    public Sesion getSesionById(Long id) {
        return sesionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));
    }
    
    public SesionDetalleResponse convertirASesionDetalleResponse(Sesion sesion) {
        // Obtener estadísticas detalladas
        SesionDetalleResponse.EstadisticasDetalladas estadisticas = obtenerEstadisticasDetalladas(sesion.getId());
        
        // Obtener detalles de asistencia
        List<SesionDetalleResponse.AsistenciaDetalle> asistencias = obtenerAsistenciasDetalle(sesion.getId());
        
        return SesionDetalleResponse.builder()
            .id(sesion.getId())
            .nombre(sesion.getNombre())
            .descripcion(sesion.getDescripcion())
            .aula(sesion.getAula())
            .estado(sesion.getEstado())
            .radioProximidad(sesion.getRadioProximidad())
            .fechaInicio(sesion.getFechaInicio())
            .fechaFin(sesion.getFechaFin())
            .fechaCierre(sesion.getFechaCierre())
            .coordenadasProfesor(SesionDetalleResponse.CoordenadasProfesor.builder()
                .latitud(sesion.getLatitudProfesor())
                .longitud(sesion.getLongitudProfesor())
                .build())
            .curso(SesionDetalleResponse.CursoCompleto.builder()
                .id(sesion.getCurso().getId())
                .codigo(sesion.getCurso().getCodigo())
                .nombre(sesion.getCurso().getNombre())
                .descripcion(sesion.getCurso().getDescripcion())
                .turno(sesion.getCurso().getTurno())
                .seccion(sesion.getCurso().getSeccion())
                .periodo(sesion.getCurso().getPeriodo())
                .aula(sesion.getCurso().getAula())
                .horario(sesion.getCurso().getHorario())
                .año(sesion.getCurso().getAño())
                .semestre(sesion.getCurso().getSemestre())
                .asignatura(SesionDetalleResponse.AsignaturaInfo.builder()
                    .id(sesion.getCurso().getAsignatura().getId())
                    .codigo(sesion.getCurso().getAsignatura().getCodigo())
                    .nombre(sesion.getCurso().getAsignatura().getNombre())
                    .descripcion(sesion.getCurso().getAsignatura().getDescripcion())
                    .creditos(sesion.getCurso().getAsignatura().getCreditos())
                    .build())
                .build())
            .profesor(SesionDetalleResponse.ProfesorCompleto.builder()
                .id(sesion.getProfesor().getId())
                .identificacion(sesion.getProfesor().getIdentificacion())
                .nombre(sesion.getProfesor().getNombre())
                .apellidos(sesion.getProfesor().getApellido())
                .correo(sesion.getProfesor().getCorreo())
                .programa(sesion.getProfesor().getPrograma())
                .build())
            .estadisticas(estadisticas)
            .asistencias(asistencias)
            .build();
    }
    
    private SesionDetalleResponse.EstadisticasDetalladas obtenerEstadisticasDetalladas(Long idSesion) {
        try {
            EstadisticasSesionDTO stats = getEstadisticasSesion(idSesion);
            
            // Calcular porcentaje de estudiantes que firmaron
            Double porcentajeFirmaron = stats.getTotalInscritos() > 0 ? 
                (double) stats.getTotalFirmaron() / stats.getTotalInscritos() * 100 : 0.0;
            
            // Calcular estudiantes en rango vs fuera de rango
            Long estudiantesEnRango = stats.getPresentes() + stats.getTardios();
            Long estudiantesFueraRango = stats.getFueraRango();
            
            return SesionDetalleResponse.EstadisticasDetalladas.builder()
                .totalInscritos(stats.getTotalInscritos())
                .totalFirmaron(stats.getTotalFirmaron())
                .presentes(stats.getPresentes())
                .ausentes(stats.getAusentes())
                .tardios(stats.getTardios())
                .fueraRango(stats.getFueraRango())
                .porcentajeAsistencia(stats.getPorcentajeAsistencia())
                .porcentajeFirmaron(porcentajeFirmaron)
                .estudiantesEnRango(estudiantesEnRango)
                .estudiantesFueraRango(estudiantesFueraRango)
                .build();
        } catch (Exception e) {
            // Si hay error, devolver estadísticas vacías
            return SesionDetalleResponse.EstadisticasDetalladas.builder()
                .totalInscritos(0L)
                .totalFirmaron(0L)
                .presentes(0L)
                .ausentes(0L)
                .tardios(0L)
                .fueraRango(0L)
                .porcentajeAsistencia(0.0)
                .porcentajeFirmaron(0.0)
                .estudiantesEnRango(0L)
                .estudiantesFueraRango(0L)
                .build();
        }
    }
    
    private List<SesionDetalleResponse.AsistenciaDetalle> obtenerAsistenciasDetalle(Long idSesion) {
        try {
            List<SesionDetalleDTO> detalles = obtenerAsistenciaSesion(idSesion);
            
            return detalles.stream()
                .map(detalle -> SesionDetalleResponse.AsistenciaDetalle.builder()
                    .id(detalle.getId())
                    .estudianteNombre(detalle.getEstudiante())
                    .estudianteIdentificacion("") // Se puede agregar si está disponible
                    .estudianteCorreo("") // Se puede agregar si está disponible
                    .estado(detalle.getEstado().toString())
                    .fechaFirma(detalle.getFechaFirma())
                    .latitudEstudiante(0.0) // Se puede agregar si está disponible
                    .longitudEstudiante(0.0) // Se puede agregar si está disponible
                    .distanciaMetros(detalle.getDistanciaMetros())
                    .observacion(detalle.getObservacion())
                    .enRango(detalle.getDistanciaMetros() <= 6.0)
                    .build())
                .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of(); // Devolver lista vacía si hay error
        }
    }
    
    public List<SesionDetalleDTO> obtenerAsistenciaSesion(Long idSesion) {
        List<SesionDetalle> detalles = sesionDetalleRepository.findBySesionId(idSesion);
        
        return detalles.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    public EstadisticasSesionDTO getEstadisticasSesion(Long idSesion) {
        Sesion sesion = getSesionById(idSesion);
        
        // Contar estudiantes inscritos
        Long totalInscritos = usuarioCursoRepository.countByCursoIdAndEstadoActivo(sesion.getCurso().getId());
        
        // Contar detalles de asistencia
        Long totalFirmaron = sesionDetalleRepository.countBySesionId(idSesion);
        Long presentes = sesionDetalleRepository.countBySesionIdAndEstado(idSesion, EstadoAsistencia.PRESENTE);
        Long ausentes = sesionDetalleRepository.countBySesionIdAndEstado(idSesion, EstadoAsistencia.AUSENTE);
        Long tardios = sesionDetalleRepository.countBySesionIdAndEstado(idSesion, EstadoAsistencia.TARDIO);
        Long fueraRango = sesionDetalleRepository.countBySesionIdAndEstado(idSesion, EstadoAsistencia.FUERA_RANGO);
        
        Double porcentajeAsistencia = totalInscritos > 0 ? 
            (double) presentes / totalInscritos * 100 : 0.0;
        
        return EstadisticasSesionDTO.builder()
            .totalInscritos(totalInscritos)
            .totalFirmaron(totalFirmaron)
            .presentes(presentes)
            .ausentes(ausentes)
            .tardios(tardios)
            .fueraRango(fueraRango)
            .porcentajeAsistencia(porcentajeAsistencia)
            .build();
    }
    
    public Sesion actualizarSesion(Long id, ActualizarSesionRequest request) {
        Sesion sesion = getSesionById(id);
        
        // Validar que la sesión esté activa para actualizar
        if (!"ACTIVA".equals(sesion.getEstado())) {
            throw new RuntimeException("Solo se pueden actualizar sesiones que estén en estado ACTIVA. Estado actual: " + sesion.getEstado());
        }
        
        if (request.getLatitudProfesor() != null) {
            sesion.setLatitudProfesor(request.getLatitudProfesor());
        }
        if (request.getLongitudProfesor() != null) {
            sesion.setLongitudProfesor(request.getLongitudProfesor());
        }
        if (request.getRadioProximidad() != null) {
            sesion.setRadioProximidad(request.getRadioProximidad());
        }
        
        return sesionRepository.save(sesion);
    }
    
    public SesionActualizadaResponse convertirASesionActualizadaResponse(Sesion sesion) {
        return SesionActualizadaResponse.builder()
            .id(sesion.getId())
            .nombre(sesion.getNombre())
            .descripcion(sesion.getDescripcion())
            .aula(sesion.getAula())
            .estado(sesion.getEstado())
            .radioProximidad(sesion.getRadioProximidad())
            .fechaInicio(sesion.getFechaInicio())
            .fechaFin(sesion.getFechaFin())
            .fechaCierre(sesion.getFechaCierre())
            .coordenadasProfesor(SesionActualizadaResponse.CoordenadasActualizadas.builder()
                .latitud(sesion.getLatitudProfesor())
                .longitud(sesion.getLongitudProfesor())
                .radioProximidad(sesion.getRadioProximidad())
                .fechaActualizacion(LocalDateTime.now())
                .build())
            .curso(SesionActualizadaResponse.ResumenCurso.builder()
                .id(sesion.getCurso().getId())
                .codigo(sesion.getCurso().getCodigo())
                .nombre(sesion.getCurso().getNombre())
                .turno(sesion.getCurso().getTurno())
                .seccion(sesion.getCurso().getSeccion())
                .periodo(sesion.getCurso().getPeriodo())
                .build())
            .profesor(SesionActualizadaResponse.ResumenProfesor.builder()
                .id(sesion.getProfesor().getId())
                .identificacion(sesion.getProfesor().getIdentificacion())
                .nombre(sesion.getProfesor().getNombre())
                .apellidos(sesion.getProfesor().getApellido())
                .build())
            .mensajeActualizacion("Sesión actualizada exitosamente")
            .build();
    }
    
    public void eliminarSesion(Long id) {
        Sesion sesion = getSesionById(id);
        
        // Verificar si tiene asistencias registradas
        Long countAsistencias = sesionDetalleRepository.countBySesionId(id);
        if (countAsistencias > 0) {
            throw new RuntimeException("No se puede eliminar una sesión que tiene asistencias registradas");
        }
        
        sesionRepository.delete(sesion);
    }
    
    public SesionEliminadaResponse convertirASesionEliminadaResponse(Sesion sesion) {
        // Obtener estadísticas antes de eliminar
        Long totalAsistencias = sesionDetalleRepository.countBySesionId(sesion.getId());
        Long estudiantesAfectados = usuarioCursoRepository.countByCursoIdAndEstadoActivo(sesion.getCurso().getId());
        
        return SesionEliminadaResponse.builder()
            .idEliminado(sesion.getId())
            .nombreSesion(sesion.getNombre())
            .estadoEliminacion("ELIMINADA")
            .fechaEliminacion(LocalDateTime.now())
            .curso(SesionEliminadaResponse.ResumenCurso.builder()
                .id(sesion.getCurso().getId())
                .codigo(sesion.getCurso().getCodigo())
                .nombre(sesion.getCurso().getNombre())
                .turno(sesion.getCurso().getTurno())
                .seccion(sesion.getCurso().getSeccion())
                .periodo(sesion.getCurso().getPeriodo())
                .build())
            .profesor(SesionEliminadaResponse.ResumenProfesor.builder()
                .id(sesion.getProfesor().getId())
                .identificacion(sesion.getProfesor().getIdentificacion())
                .nombre(sesion.getProfesor().getNombre())
                .apellidos(sesion.getProfesor().getApellido())
                .build())
            .estadisticas(SesionEliminadaResponse.EstadisticasEliminacion.builder()
                .totalAsistenciasRegistradas(totalAsistencias)
                .estudiantesAfectados(estudiantesAfectados)
                .impactoEliminacion(totalAsistencias > 0 ? "ALTO" : "BAJO")
                .datosPerdidos(totalAsistencias > 0)
                .build())
            .mensajeEliminacion("Sesión eliminada exitosamente")
            .build();
    }
    
    public ReporteAsistenciaResponse convertirAReporteAsistenciaResponse(Sesion sesion) {
        // Obtener estadísticas generales
        EstadisticasSesionDTO stats = getEstadisticasSesion(sesion.getId());
        
        // Obtener detalles de asistencia
        List<SesionDetalleDTO> detalles = obtenerAsistenciaSesion(sesion.getId());
        
        // Convertir a DTO optimizado
        List<ReporteAsistenciaResponse.DetalleAsistencia> asistencias = detalles.stream()
            .map(this::convertirADetalleAsistencia)
            .collect(Collectors.toList());
        
        // Calcular resumen geográfico
        ReporteAsistenciaResponse.ResumenGeografico resumenGeo = calcularResumenGeografico(detalles);
        
        return ReporteAsistenciaResponse.builder()
            .idSesion(sesion.getId())
            .nombreSesion(sesion.getNombre())
            .estadoSesion(sesion.getEstado())
            .fechaInicio(sesion.getFechaInicio())
            .fechaFin(sesion.getFechaFin())
            .fechaCierre(sesion.getFechaCierre())
            .curso(ReporteAsistenciaResponse.ResumenCurso.builder()
                .id(sesion.getCurso().getId())
                .codigo(sesion.getCurso().getCodigo())
                .nombre(sesion.getCurso().getNombre())
                .turno(sesion.getCurso().getTurno())
                .seccion(sesion.getCurso().getSeccion())
                .periodo(sesion.getCurso().getPeriodo())
                .aula(sesion.getCurso().getAula())
                .horario(sesion.getCurso().getHorario())
                .build())
            .profesor(ReporteAsistenciaResponse.ResumenProfesor.builder()
                .id(sesion.getProfesor().getId())
                .identificacion(sesion.getProfesor().getIdentificacion())
                .nombre(sesion.getProfesor().getNombre())
                .apellidos(sesion.getProfesor().getApellido())
                .correo(sesion.getProfesor().getCorreo())
                .build())
            .estadisticas(ReporteAsistenciaResponse.EstadisticasGenerales.builder()
                .totalInscritos(stats.getTotalInscritos())
                .totalFirmaron(stats.getTotalFirmaron())
                .presentes(stats.getPresentes())
                .ausentes(stats.getAusentes())
                .tardios(stats.getTardios())
                .fueraRango(stats.getFueraRango())
                .porcentajeAsistencia(stats.getPorcentajeAsistencia())
                .porcentajeFirmaron(stats.getTotalInscritos() > 0 ? 
                    (double) stats.getTotalFirmaron() / stats.getTotalInscritos() * 100 : 0.0)
                .duracionMinutos(calcularDuracionMinutos(sesion))
                .build())
            .asistencias(asistencias)
            .resumenGeografico(resumenGeo)
            .build();
    }
    
    public EstadisticasSesionResponse convertirAEstadisticasSesionResponse(Sesion sesion) {
        // Obtener estadísticas detalladas
        EstadisticasSesionDTO stats = getEstadisticasSesion(sesion.getId());
        
        // Calcular análisis temporal
        EstadisticasSesionResponse.AnalisisTemporal analisisTemporal = calcularAnalisisTemporal(sesion);
        
        // Calcular análisis geográfico
        EstadisticasSesionResponse.AnalisisGeografico analisisGeo = calcularAnalisisGeografico(sesion.getId());
        
        // Generar recomendaciones
        EstadisticasSesionResponse.Recomendaciones recomendaciones = generarRecomendaciones(stats, analisisTemporal, analisisGeo);
        
        return EstadisticasSesionResponse.builder()
            .idSesion(sesion.getId())
            .nombreSesion(sesion.getNombre())
            .estadoSesion(sesion.getEstado())
            .fechaInicio(sesion.getFechaInicio())
            .fechaFin(sesion.getFechaFin())
            .fechaCierre(sesion.getFechaCierre())
            .curso(EstadisticasSesionResponse.ResumenCurso.builder()
                .id(sesion.getCurso().getId())
                .codigo(sesion.getCurso().getCodigo())
                .nombre(sesion.getCurso().getNombre())
                .turno(sesion.getCurso().getTurno())
                .seccion(sesion.getCurso().getSeccion())
                .periodo(sesion.getCurso().getPeriodo())
                .build())
            .profesor(EstadisticasSesionResponse.ResumenProfesor.builder()
                .id(sesion.getProfesor().getId())
                .identificacion(sesion.getProfesor().getIdentificacion())
                .nombre(sesion.getProfesor().getNombre())
                .apellidos(sesion.getProfesor().getApellido())
                .build())
            .estadisticas(EstadisticasSesionResponse.EstadisticasDetalladas.builder()
                .totalInscritos(stats.getTotalInscritos())
                .totalFirmaron(stats.getTotalFirmaron())
                .presentes(stats.getPresentes())
                .ausentes(stats.getAusentes())
                .tardios(stats.getTardios())
                .fueraRango(stats.getFueraRango())
                .porcentajeAsistencia(stats.getPorcentajeAsistencia())
                .porcentajeFirmaron(stats.getTotalInscritos() > 0 ? 
                    (double) stats.getTotalFirmaron() / stats.getTotalInscritos() * 100 : 0.0)
                .porcentajeAusentes(stats.getTotalInscritos() > 0 ? 
                    (double) stats.getAusentes() / stats.getTotalInscritos() * 100 : 0.0)
                .porcentajeTardios(stats.getTotalInscritos() > 0 ? 
                    (double) stats.getTardios() / stats.getTotalInscritos() * 100 : 0.0)
                .duracionMinutos(calcularDuracionMinutos(sesion))
                .estadoFinal(determinarEstadoFinal(stats))
                .calificacionAsistencia(determinarCalificacionAsistencia(stats.getPorcentajeAsistencia()))
                .build())
            .analisisTemporal(analisisTemporal)
            .analisisGeografico(analisisGeo)
            .recomendaciones(recomendaciones)
            .build();
    }
    
    public SesionDetalle registrarAsistencia(RegistrarAsistenciaRequest request) {
        // Validaciones robustas
        validarRegistroAsistencia(request);
        
        Sesion sesion = getSesionById(request.getIdSesion());
        Usuario estudiante = usuarioRepository.findById(request.getIdEstudiante())
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        
        // Calcular distancia
        double distancia = GeoUtils.calcularDistancia(
            sesion.getLatitudProfesor(), sesion.getLongitudProfesor(),
            request.getLatitudEstudiante(), request.getLongitudEstudiante()
        );
        
        // Determinar estado según distancia y tiempo
        EstadoAsistencia estado = determinarEstadoAsistencia(distancia, sesion.getRadioProximidad(), sesion.getFechaInicio());
        
        SesionDetalle detalle = SesionDetalle.builder()
            .sesion(sesion)
            .estudiante(estudiante)
            .fechaFirma(LocalDateTime.now())
            .estado(estado)
            .latitudEstudiante(request.getLatitudEstudiante())
            .longitudEstudiante(request.getLongitudEstudiante())
            .distanciaMetros(distancia)
            .observacion(request.getObservacion())
            .build();
        
        return sesionDetalleRepository.save(detalle);
    }
    
    private void validarRegistroAsistencia(RegistrarAsistenciaRequest request) {
        // Validar que la sesión existe y está activa
        Sesion sesion = getSesionById(request.getIdSesion());
        
        if (!"ACTIVA".equals(sesion.getEstado())) {
            throw new RuntimeException("La sesión no está activa para firmar. Estado actual: " + sesion.getEstado());
        }
        
        // Validar que la sesión no haya terminado
        if (sesion.getFechaFin() != null && LocalDateTime.now().isAfter(sesion.getFechaFin())) {
            throw new RuntimeException("La sesión ya ha terminado. No se pueden registrar más asistencias");
        }
        
        // Validar que la sesión haya comenzado
        if (sesion.getFechaInicio() != null && LocalDateTime.now().isBefore(sesion.getFechaInicio())) {
            throw new RuntimeException("La sesión aún no ha comenzado. Intente más tarde");
        }
        
        // Validar que el estudiante existe
        usuarioRepository.findById(request.getIdEstudiante())
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        
        // Validar que el estudiante está inscrito en el curso
        UsuarioCurso inscripcion = usuarioCursoRepository.findByUsuarioIdAndCursoId(
            request.getIdEstudiante(), sesion.getCurso().getId());
        
        if (inscripcion == null) {
            throw new RuntimeException("El estudiante no está inscrito en este curso");
        }
        
        // Validar que la inscripción está activa
        if (!"ACTIVO".equals(inscripcion.getEstado())) {
            throw new RuntimeException("La inscripción del estudiante no está activa");
        }
        
        // Validar que no haya firmado previamente
        SesionDetalle detalleExistente = sesionDetalleRepository.findBySesionIdAndEstudianteId(
            request.getIdSesion(), request.getIdEstudiante());
        
        if (detalleExistente != null) {
            throw new RuntimeException("El estudiante ya firmó en esta sesión el " + 
                detalleExistente.getFechaFirma().toString());
        }
        
        // Validar coordenadas
        validarCoordenadas(request.getLatitudEstudiante(), request.getLongitudEstudiante());
        
        // Validar distancia máxima razonable (6 metros - radio de proximidad)
        double distancia = GeoUtils.calcularDistancia(
            sesion.getLatitudProfesor(), sesion.getLongitudProfesor(),
            request.getLatitudEstudiante(), request.getLongitudEstudiante()
        );
        
        if (distancia > sesion.getRadioProximidad()) {
            throw new RuntimeException("La ubicación del estudiante está fuera del radio permitido. " +
                "Distancia: " + String.format("%.1f", distancia) + " metros. Radio permitido: " + 
                String.format("%.1f", sesion.getRadioProximidad()) + " metros");
        }
    }
    
    private void validarCoordenadas(Double latitud, Double longitud) {
        if (latitud == null || longitud == null) {
            throw new RuntimeException("Las coordenadas del estudiante son obligatorias");
        }
        
        if (latitud < -90.0 || latitud > 90.0) {
            throw new RuntimeException("La latitud debe estar entre -90 y 90 grados");
        }
        
        if (longitud < -180.0 || longitud > 180.0) {
            throw new RuntimeException("La longitud debe estar entre -180 y 180 grados");
        }
    }
    
    private EstadoAsistencia determinarEstadoAsistencia(double distancia, double radioPermitido, LocalDateTime fechaInicio) {
        LocalDateTime ahora = LocalDateTime.now();
        long minutosTranscurridos = java.time.Duration.between(fechaInicio, ahora).toMinutes();
        
        if (distancia <= radioPermitido) {
            if (minutosTranscurridos <= 15) {
                return EstadoAsistencia.PRESENTE;
            } else {
                return EstadoAsistencia.TARDIO;
            }
        } else {
            return EstadoAsistencia.FUERA_RANGO;
        }
    }
    
    public AsistenciaRegistradaResponse convertirAAsistenciaRegistradaResponse(SesionDetalle detalle) {
        // Obtener información adicional
        Sesion sesion = detalle.getSesion();
        Usuario estudiante = detalle.getEstudiante();
        Curso curso = sesion.getCurso();
        Usuario profesor = sesion.getProfesor();
        
        // Calcular análisis de asistencia
        AsistenciaRegistradaResponse.AnalisisAsistencia analisis = calcularAnalisisAsistencia(detalle, sesion);
        
        // Generar recomendaciones
        AsistenciaRegistradaResponse.RecomendacionesEstudiante recomendaciones = generarRecomendacionesEstudiante(detalle, sesion);
        
        return AsistenciaRegistradaResponse.builder()
            .id(detalle.getId())
            .estado(detalle.getEstado().toString())
            .fechaFirma(detalle.getFechaFirma())
            .distanciaMetros(detalle.getDistanciaMetros())
            .enRango(detalle.getDistanciaMetros() <= sesion.getRadioProximidad())
            .mensajeValidacion(generarMensajeValidacion(detalle, sesion))
            .estudiante(AsistenciaRegistradaResponse.InformacionEstudiante.builder()
                .id(estudiante.getId())
                .identificacion(estudiante.getIdentificacion())
                .nombre(estudiante.getNombre())
                .apellidos(estudiante.getApellido())
                .correo(estudiante.getCorreo())
                .programa(estudiante.getPrograma())
                .build())
            .sesion(AsistenciaRegistradaResponse.InformacionSesion.builder()
                .id(sesion.getId())
                .nombre(sesion.getNombre())
                .estado(sesion.getEstado())
                .fechaInicio(sesion.getFechaInicio())
                .fechaFin(sesion.getFechaFin())
                .radioProximidad(sesion.getRadioProximidad())
                .aula(sesion.getAula())
                .build())
            .curso(AsistenciaRegistradaResponse.InformacionCurso.builder()
                .id(curso.getId())
                .codigo(curso.getCodigo())
                .nombre(curso.getNombre())
                .turno(curso.getTurno())
                .seccion(curso.getSeccion())
                .periodo(curso.getPeriodo())
                .horario(curso.getHorario())
                .build())
            .profesor(AsistenciaRegistradaResponse.InformacionProfesor.builder()
                .id(profesor.getId())
                .identificacion(profesor.getIdentificacion())
                .nombre(profesor.getNombre())
                .apellidos(profesor.getApellido())
                .build())
            .analisis(analisis)
            .recomendaciones(recomendaciones)
            .build();
    }
    
    private AsistenciaRegistradaResponse.AnalisisAsistencia calcularAnalisisAsistencia(SesionDetalle detalle, Sesion sesion) {
        long minutosTranscurridos = java.time.Duration.between(sesion.getFechaInicio(), detalle.getFechaFirma()).toMinutes();
        
        // Determinar calidad de asistencia
        String calidadAsistencia = determinarCalidadAsistencia(detalle.getDistanciaMetros(), minutosTranscurridos);
        
        // Determinar puntualidad
        String puntualidad = determinarPuntualidad(minutosTranscurridos);
        
        // Determinar precisión geográfica
        String precisionGeografica = determinarPrecisionGeografica(detalle.getDistanciaMetros(), sesion.getRadioProximidad());
        
        // Calcular tiempo transcurrido
        String tiempoTranscurrido = calcularTiempoTranscurrido(detalle.getFechaFirma());
        
        // Verificar si es la primera firma
        boolean esPrimeraFirma = esPrimeraFirmaEnSesion(detalle.getSesion().getId());
        
        // Calcular posición en la lista
        int posicionEnLista = calcularPosicionEnLista(detalle.getSesion().getId(), detalle.getFechaFirma());
        
        // Generar mensaje de análisis
        String mensajeAnalisis = generarMensajeAnalisis(calidadAsistencia, puntualidad, precisionGeografica);
        
        return AsistenciaRegistradaResponse.AnalisisAsistencia.builder()
            .calidadAsistencia(calidadAsistencia)
            .puntualidad(puntualidad)
            .precisionGeografica(precisionGeografica)
            .tiempoTranscurrido(tiempoTranscurrido)
            .esPrimeraFirma(esPrimeraFirma)
            .posicionEnLista(posicionEnLista)
            .mensajeAnalisis(mensajeAnalisis)
            .build();
    }
    
    private AsistenciaRegistradaResponse.RecomendacionesEstudiante generarRecomendacionesEstudiante(SesionDetalle detalle, Sesion sesion) {
        String recomendacionGeneral = generarRecomendacionGeneralEstudiante(detalle, sesion);
        String recomendacionUbicacion = generarRecomendacionUbicacionEstudiante(detalle, sesion);
        String recomendacionTiempo = generarRecomendacionTiempoEstudiante(detalle, sesion);
        
        List<String> accionesSugeridas = generarAccionesSugeridasEstudiante(detalle, sesion);
        
        return AsistenciaRegistradaResponse.RecomendacionesEstudiante.builder()
            .recomendacionGeneral(recomendacionGeneral)
            .recomendacionUbicacion(recomendacionUbicacion)
            .recomendacionTiempo(recomendacionTiempo)
            .accionesSugeridas(accionesSugeridas)
            .build();
    }
    
    // Métodos auxiliares para análisis
    private String determinarCalidadAsistencia(double distancia, long minutosTranscurridos) {
        if (distancia <= 3.0 && minutosTranscurridos <= 10) return "EXCELENTE";
        if (distancia <= 6.0 && minutosTranscurridos <= 15) return "BUENA";
        if (distancia <= 10.0 && minutosTranscurridos <= 30) return "REGULAR";
        return "BAJA";
    }
    
    private String determinarPuntualidad(long minutosTranscurridos) {
        if (minutosTranscurridos <= 5) return "MUY PUNTUAL";
        if (minutosTranscurridos <= 15) return "PUNTUAL";
        if (minutosTranscurridos <= 30) return "TARDIO";
        return "MUY TARDIO";
    }
    
    private String determinarPrecisionGeografica(double distancia, double radioPermitido) {
        if (distancia <= radioPermitido * 0.5) return "EXCELENTE";
        if (distancia <= radioPermitido) return "BUENA";
        if (distancia <= radioPermitido * 1.5) return "REGULAR";
        return "BAJA";
    }
    
    private boolean esPrimeraFirmaEnSesion(Long idSesion) {
        return sesionDetalleRepository.countBySesionId(idSesion) == 1;
    }
    
    private int calcularPosicionEnLista(Long idSesion, LocalDateTime fechaFirma) {
        List<SesionDetalle> detalles = sesionDetalleRepository.findBySesionId(idSesion);
        detalles.sort((a, b) -> a.getFechaFirma().compareTo(b.getFechaFirma()));
        
        for (int i = 0; i < detalles.size(); i++) {
            if (detalles.get(i).getFechaFirma().equals(fechaFirma)) {
                return i + 1;
            }
        }
        return 0;
    }
    
    private String generarMensajeAnalisis(String calidad, String puntualidad, String precision) {
        return String.format("Asistencia %s - %s - Precisión geográfica %s", calidad, puntualidad, precision);
    }
    
    private String generarMensajeValidacion(SesionDetalle detalle, Sesion sesion) {
        if (detalle.getDistanciaMetros() <= sesion.getRadioProximidad()) {
            return "Asistencia registrada exitosamente dentro del radio permitido";
        } else {
            return "Asistencia registrada fuera del radio permitido";
        }
    }
    
    private String generarRecomendacionGeneralEstudiante(SesionDetalle detalle, Sesion sesion) {
        if (detalle.getDistanciaMetros() <= sesion.getRadioProximidad()) {
            return "Excelente asistencia, mantén este nivel de puntualidad";
        } else {
            return "Considera llegar más cerca del aula para futuras sesiones";
        }
    }
    
    private String generarRecomendacionUbicacionEstudiante(SesionDetalle detalle, Sesion sesion) {
        if (detalle.getDistanciaMetros() > sesion.getRadioProximidad()) {
            return "Intenta ubicarte más cerca del aula la próxima vez";
        }
        return "Ubicación adecuada";
    }
    
    private String generarRecomendacionTiempoEstudiante(SesionDetalle detalle, Sesion sesion) {
        long minutosTranscurridos = java.time.Duration.between(sesion.getFechaInicio(), detalle.getFechaFirma()).toMinutes();
        if (minutosTranscurridos > 15) {
            return "Intenta llegar más temprano a las próximas sesiones";
        }
        return "Tiempo de llegada adecuado";
    }
    
    private List<String> generarAccionesSugeridasEstudiante(SesionDetalle detalle, Sesion sesion) {
        List<String> acciones = new java.util.ArrayList<>();
        
        if (detalle.getDistanciaMetros() > sesion.getRadioProximidad()) {
            acciones.add("Revisar la ubicación del aula en el mapa del campus");
            acciones.add("Llegar con más anticipación para ubicarse correctamente");
        }
        
        long minutosTranscurridos = java.time.Duration.between(sesion.getFechaInicio(), detalle.getFechaFirma()).toMinutes();
        if (minutosTranscurridos > 15) {
            acciones.add("Configurar recordatorios para llegar más temprano");
            acciones.add("Revisar el horario de transporte");
        }
        
        if (acciones.isEmpty()) {
            acciones.add("Mantener el buen rendimiento");
        }
        
        return acciones;
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
    
    // Métodos auxiliares para las nuevas respuestas
    private ReporteAsistenciaResponse.DetalleAsistencia convertirADetalleAsistencia(SesionDetalleDTO detalle) {
        return ReporteAsistenciaResponse.DetalleAsistencia.builder()
            .id(detalle.getId())
            .estudianteNombre(detalle.getEstudiante())
            .estudianteIdentificacion("") // Se puede agregar si está disponible
            .estudianteCorreo("") // Se puede agregar si está disponible
            .estado(detalle.getEstado().toString())
            .fechaFirma(detalle.getFechaFirma())
            .latitudEstudiante(0.0) // Se puede agregar si está disponible
            .longitudEstudiante(0.0) // Se puede agregar si está disponible
            .distanciaMetros(detalle.getDistanciaMetros())
            .observacion(detalle.getObservacion())
            .enRango(detalle.getDistanciaMetros() <= 6.0)
            .tiempoTranscurrido(calcularTiempoTranscurrido(detalle.getFechaFirma()))
            .build();
    }
    
    private ReporteAsistenciaResponse.ResumenGeografico calcularResumenGeografico(List<SesionDetalleDTO> detalles) {
        if (detalles.isEmpty()) {
            return ReporteAsistenciaResponse.ResumenGeografico.builder()
                .promedioDistancia(0.0)
                .distanciaMinima(0.0)
                .distanciaMaxima(0.0)
                .estudiantesEnRango(0L)
                .estudiantesFueraRango(0L)
                .mensajeGeografico("No hay datos geográficos disponibles")
                .build();
        }
        
        Double promedioDistancia = detalles.stream()
            .mapToDouble(SesionDetalleDTO::getDistanciaMetros)
            .average()
            .orElse(0.0);
        
        Double distanciaMinima = detalles.stream()
            .mapToDouble(SesionDetalleDTO::getDistanciaMetros)
            .min()
            .orElse(0.0);
        
        Double distanciaMaxima = detalles.stream()
            .mapToDouble(SesionDetalleDTO::getDistanciaMetros)
            .max()
            .orElse(0.0);
        
        Long estudiantesEnRango = detalles.stream()
            .filter(d -> d.getDistanciaMetros() <= 6.0)
            .count();
        
        Long estudiantesFueraRango = detalles.stream()
            .filter(d -> d.getDistanciaMetros() > 6.0)
            .count();
        
        String mensajeGeografico = String.format("Distancia promedio: %.1f metros. %d estudiantes en rango, %d fuera de rango", 
            promedioDistancia, estudiantesEnRango, estudiantesFueraRango);
        
        return ReporteAsistenciaResponse.ResumenGeografico.builder()
            .promedioDistancia(promedioDistancia)
            .distanciaMinima(distanciaMinima)
            .distanciaMaxima(distanciaMaxima)
            .estudiantesEnRango(estudiantesEnRango)
            .estudiantesFueraRango(estudiantesFueraRango)
            .mensajeGeografico(mensajeGeografico)
            .build();
    }
    
    private Long calcularDuracionMinutos(Sesion sesion) {
        LocalDateTime inicio = sesion.getFechaInicio();
        LocalDateTime fin = sesion.getFechaCierre() != null ? sesion.getFechaCierre() : 
                           sesion.getFechaFin() != null ? sesion.getFechaFin() : LocalDateTime.now();
        return java.time.Duration.between(inicio, fin).toMinutes();
    }
    
    private String calcularTiempoTranscurrido(LocalDateTime fechaFirma) {
        long minutos = java.time.Duration.between(fechaFirma, LocalDateTime.now()).toMinutes();
        if (minutos < 60) {
            return minutos + " minutos";
        } else {
            long horas = minutos / 60;
            long minutosRestantes = minutos % 60;
            return horas + " horas " + minutosRestantes + " minutos";
        }
    }
    
    private EstadisticasSesionResponse.AnalisisTemporal calcularAnalisisTemporal(Sesion sesion) {
        try {
            List<SesionDetalleDTO> detalles = obtenerAsistenciaSesion(sesion.getId());
            
            if (detalles.isEmpty()) {
                return EstadisticasSesionResponse.AnalisisTemporal.builder()
                    .primeraFirma(null)
                    .ultimaFirma(null)
                    .minutosPrimeraFirma(0L)
                    .minutosUltimaFirma(0L)
                    .patronFirmas("Sin firmas registradas")
                    .recomendacionTemporal("No hay datos temporales disponibles")
                    .build();
            }
            
            LocalDateTime primeraFirma = detalles.stream()
                .map(SesionDetalleDTO::getFechaFirma)
                .min(LocalDateTime::compareTo)
                .orElse(null);
            
            LocalDateTime ultimaFirma = detalles.stream()
                .map(SesionDetalleDTO::getFechaFirma)
                .max(LocalDateTime::compareTo)
                .orElse(null);
            
            Long minutosPrimeraFirma = primeraFirma != null ? 
                java.time.Duration.between(sesion.getFechaInicio(), primeraFirma).toMinutes() : 0L;
            
            Long minutosUltimaFirma = ultimaFirma != null ? 
                java.time.Duration.between(sesion.getFechaInicio(), ultimaFirma).toMinutes() : 0L;
            
            String patronFirmas = determinarPatronFirmas(detalles.size(), minutosPrimeraFirma, minutosUltimaFirma);
            String recomendacionTemporal = generarRecomendacionTemporal(minutosPrimeraFirma, minutosUltimaFirma);
            
            return EstadisticasSesionResponse.AnalisisTemporal.builder()
                .primeraFirma(primeraFirma)
                .ultimaFirma(ultimaFirma)
                .minutosPrimeraFirma(minutosPrimeraFirma)
                .minutosUltimaFirma(minutosUltimaFirma)
                .patronFirmas(patronFirmas)
                .recomendacionTemporal(recomendacionTemporal)
                .build();
        } catch (Exception e) {
            return EstadisticasSesionResponse.AnalisisTemporal.builder()
                .primeraFirma(null)
                .ultimaFirma(null)
                .minutosPrimeraFirma(0L)
                .minutosUltimaFirma(0L)
                .patronFirmas("Error al calcular")
                .recomendacionTemporal("Error en análisis temporal")
                .build();
        }
    }
    
    private EstadisticasSesionResponse.AnalisisGeografico calcularAnalisisGeografico(Long idSesion) {
        try {
            List<SesionDetalleDTO> detalles = obtenerAsistenciaSesion(idSesion);
            
            if (detalles.isEmpty()) {
                return EstadisticasSesionResponse.AnalisisGeografico.builder()
                    .promedioDistancia(0.0)
                    .distanciaMinima(0.0)
                    .distanciaMaxima(0.0)
                    .estudiantesEnRango(0L)
                    .estudiantesFueraRango(0L)
                    .porcentajeEnRango(0.0)
                    .calidadGeografica("Sin datos")
                    .recomendacionGeografica("No hay datos geográficos disponibles")
                    .build();
            }
            
            Double promedioDistancia = detalles.stream()
                .mapToDouble(SesionDetalleDTO::getDistanciaMetros)
                .average()
                .orElse(0.0);
            
            Double distanciaMinima = detalles.stream()
                .mapToDouble(SesionDetalleDTO::getDistanciaMetros)
                .min()
                .orElse(0.0);
            
            Double distanciaMaxima = detalles.stream()
                .mapToDouble(SesionDetalleDTO::getDistanciaMetros)
                .max()
                .orElse(0.0);
            
            Long estudiantesEnRango = detalles.stream()
                .filter(d -> d.getDistanciaMetros() <= 6.0)
                .count();
            
            Long estudiantesFueraRango = detalles.stream()
                .filter(d -> d.getDistanciaMetros() > 6.0)
                .count();
            
            Double porcentajeEnRango = detalles.size() > 0 ? 
                (double) estudiantesEnRango / detalles.size() * 100 : 0.0;
            
            String calidadGeografica = determinarCalidadGeografica(porcentajeEnRango, promedioDistancia);
            String recomendacionGeografica = generarRecomendacionGeografica(porcentajeEnRango, promedioDistancia);
            
            return EstadisticasSesionResponse.AnalisisGeografico.builder()
                .promedioDistancia(promedioDistancia)
                .distanciaMinima(distanciaMinima)
                .distanciaMaxima(distanciaMaxima)
                .estudiantesEnRango(estudiantesEnRango)
                .estudiantesFueraRango(estudiantesFueraRango)
                .porcentajeEnRango(porcentajeEnRango)
                .calidadGeografica(calidadGeografica)
                .recomendacionGeografica(recomendacionGeografica)
                .build();
        } catch (Exception e) {
            return EstadisticasSesionResponse.AnalisisGeografico.builder()
                .promedioDistancia(0.0)
                .distanciaMinima(0.0)
                .distanciaMaxima(0.0)
                .estudiantesEnRango(0L)
                .estudiantesFueraRango(0L)
                .porcentajeEnRango(0.0)
                .calidadGeografica("Error")
                .recomendacionGeografica("Error en análisis geográfico")
                .build();
        }
    }
    
    private EstadisticasSesionResponse.Recomendaciones generarRecomendaciones(
            EstadisticasSesionDTO stats, 
            EstadisticasSesionResponse.AnalisisTemporal analisisTemporal,
            EstadisticasSesionResponse.AnalisisGeografico analisisGeo) {
        
        String recomendacionGeneral = generarRecomendacionGeneral(stats);
        String recomendacionHorario = analisisTemporal.getRecomendacionTemporal();
        String recomendacionUbicacion = analisisGeo.getRecomendacionGeografica();
        String recomendacionComunicacion = generarRecomendacionComunicacion(stats);
        
        List<String> accionesSugeridas = generarAccionesSugeridas(stats, analisisTemporal, analisisGeo);
        
        return EstadisticasSesionResponse.Recomendaciones.builder()
            .recomendacionGeneral(recomendacionGeneral)
            .recomendacionHorario(recomendacionHorario)
            .recomendacionUbicacion(recomendacionUbicacion)
            .recomendacionComunicacion(recomendacionComunicacion)
            .accionesSugeridas(accionesSugeridas)
            .build();
    }
    
    // Métodos auxiliares para análisis
    private String determinarPatronFirmas(int totalFirmas, Long minutosPrimera, Long minutosUltima) {
        if (totalFirmas == 0) return "Sin firmas";
        if (minutosUltima - minutosPrimera < 10) return "Firmas concentradas";
        if (minutosUltima - minutosPrimera > 60) return "Firmas dispersas";
        return "Firmas distribuidas";
    }
    
    private String generarRecomendacionTemporal(Long minutosPrimera, Long minutosUltima) {
        if (minutosPrimera > 15) return "Considerar dar más tiempo para llegar al aula";
        if (minutosUltima - minutosPrimera > 60) return "Las firmas están muy dispersas, considerar horarios más específicos";
        return "El patrón temporal de firmas es adecuado";
    }
    
    private String determinarCalidadGeografica(Double porcentajeEnRango, Double promedioDistancia) {
        if (porcentajeEnRango >= 90 && promedioDistancia <= 3.0) return "EXCELENTE";
        if (porcentajeEnRango >= 80 && promedioDistancia <= 5.0) return "BUENA";
        if (porcentajeEnRango >= 60 && promedioDistancia <= 8.0) return "REGULAR";
        return "BAJA";
    }
    
    private String generarRecomendacionGeografica(Double porcentajeEnRango, Double promedioDistancia) {
        if (porcentajeEnRango < 70) return "Considerar aumentar el radio de proximidad o mejorar la ubicación del aula";
        if (promedioDistancia > 10.0) return "La ubicación del aula puede estar causando problemas de acceso";
        return "La ubicación geográfica es adecuada";
    }
    
    private String generarRecomendacionGeneral(EstadisticasSesionDTO stats) {
        if (stats.getPorcentajeAsistencia() >= 80) return "Excelente asistencia, mantener las estrategias actuales";
        if (stats.getPorcentajeAsistencia() >= 60) return "Buena asistencia, considerar mejoras menores";
        if (stats.getPorcentajeAsistencia() >= 40) return "Asistencia regular, se requieren mejoras";
        return "Asistencia baja, se necesitan estrategias de mejora urgentes";
    }
    
    private String generarRecomendacionComunicacion(EstadisticasSesionDTO stats) {
        if (stats.getAusentes() > stats.getTotalInscritos() * 0.3) {
            return "Mejorar comunicación sobre horarios y ubicación de clases";
        }
        return "La comunicación parece ser efectiva";
    }
    
    private List<String> generarAccionesSugeridas(EstadisticasSesionDTO stats, 
            EstadisticasSesionResponse.AnalisisTemporal analisisTemporal,
            EstadisticasSesionResponse.AnalisisGeografico analisisGeo) {
        List<String> acciones = new java.util.ArrayList<>();
        
        if (stats.getPorcentajeAsistencia() < 70) {
            acciones.add("Enviar recordatorios de clase con mayor anticipación");
            acciones.add("Implementar sistema de notificaciones push");
        }
        
        if (analisisGeo.getPorcentajeEnRango() < 80) {
            acciones.add("Revisar ubicación del aula o aumentar radio de proximidad");
            acciones.add("Proporcionar mapas detallados del campus");
        }
        
        if (analisisTemporal.getMinutosPrimeraFirma() > 15) {
            acciones.add("Considerar horarios más flexibles");
            acciones.add("Mejorar señalización hacia el aula");
        }
        
        if (acciones.isEmpty()) {
            acciones.add("Mantener las estrategias actuales");
        }
        
        return acciones;
    }
    
    private String determinarCalificacionAsistencia(Double porcentajeAsistencia) {
        if (porcentajeAsistencia >= 90) return "A+";
        if (porcentajeAsistencia >= 80) return "A";
        if (porcentajeAsistencia >= 70) return "B";
        if (porcentajeAsistencia >= 60) return "C";
        if (porcentajeAsistencia >= 50) return "D";
        return "F";
    }
    
    public ValidacionAsistenciaResponse validarAsistencia(Long idSesion, Long idEstudiante) {
        try {
            // Obtener información de la sesión
            Sesion sesion = getSesionById(idSesion);
            
            // Obtener información del estudiante
            Usuario estudiante = usuarioRepository.findById(idEstudiante)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
            
            // Verificar inscripción
            UsuarioCurso inscripcion = usuarioCursoRepository.findByUsuarioIdAndCursoId(idEstudiante, sesion.getCurso().getId());
            
            // Verificar si ya firmó
            SesionDetalle detalleExistente = sesionDetalleRepository.findBySesionIdAndEstudianteId(idSesion, idEstudiante);
            
            // Calcular restricciones
            ValidacionAsistenciaResponse.Restricciones restricciones = calcularRestricciones(sesion, estudiante, inscripcion, detalleExistente);
            
            // Determinar si puede firmar
            boolean puedeFirmar = restricciones.getSesionActiva() && 
                                restricciones.getSesionDisponible() && 
                                restricciones.getEstudianteInscrito() && 
                                restricciones.getInscripcionActiva() && 
                                restricciones.getNoHaFirmado() && 
                                restricciones.getDentroHorario();
            
            // Generar recomendaciones
            ValidacionAsistenciaResponse.RecomendacionesValidacion recomendaciones = generarRecomendacionesValidacion(sesion, estudiante, restricciones);
            
            // Generar mensaje de validación
            String mensajeValidacion = generarMensajeValidacion(puedeFirmar, restricciones);
            
            return ValidacionAsistenciaResponse.builder()
                .puedeFirmar(puedeFirmar)
                .estadoValidacion(puedeFirmar ? "APROBADA" : "RECHAZADA")
                .mensajeValidacion(mensajeValidacion)
                .sesion(ValidacionAsistenciaResponse.InformacionSesion.builder()
                    .id(sesion.getId())
                    .nombre(sesion.getNombre())
                    .estado(sesion.getEstado())
                    .fechaInicio(sesion.getFechaInicio())
                    .fechaFin(sesion.getFechaFin())
                    .radioProximidad(sesion.getRadioProximidad())
                    .aula(sesion.getAula())
                    .sesionActiva("ACTIVA".equals(sesion.getEstado()))
                    .sesionDisponible(restricciones.getSesionDisponible())
                    .build())
                .estudiante(ValidacionAsistenciaResponse.InformacionEstudiante.builder()
                    .id(estudiante.getId())
                    .identificacion(estudiante.getIdentificacion())
                    .nombre(estudiante.getNombre())
                    .apellidos(estudiante.getApellido())
                    .correo(estudiante.getCorreo())
                    .inscritoEnCurso(inscripcion != null)
                    .inscripcionActiva(inscripcion != null && "ACTIVO".equals(inscripcion.getEstado()))
                    .yaFirmo(detalleExistente != null)
                    .fechaUltimaFirma(detalleExistente != null ? detalleExistente.getFechaFirma() : null)
                    .build())
                .curso(ValidacionAsistenciaResponse.InformacionCurso.builder()
                    .id(sesion.getCurso().getId())
                    .codigo(sesion.getCurso().getCodigo())
                    .nombre(sesion.getCurso().getNombre())
                    .turno(sesion.getCurso().getTurno())
                    .seccion(sesion.getCurso().getSeccion())
                    .periodo(sesion.getCurso().getPeriodo())
                    .horario(sesion.getCurso().getHorario())
                    .build())
                .restricciones(restricciones)
                .recomendaciones(recomendaciones)
                .build();
        } catch (Exception e) {
            return ValidacionAsistenciaResponse.builder()
                .puedeFirmar(false)
                .estadoValidacion("ERROR")
                .mensajeValidacion("Error al validar asistencia: " + e.getMessage())
                .build();
        }
    }
    
    private ValidacionAsistenciaResponse.Restricciones calcularRestricciones(Sesion sesion, Usuario estudiante, 
            UsuarioCurso inscripcion, SesionDetalle detalleExistente) {
        
        boolean sesionActiva = "ACTIVA".equals(sesion.getEstado());
        boolean sesionDisponible = sesion.getFechaFin() == null || LocalDateTime.now().isBefore(sesion.getFechaFin());
        boolean estudianteInscrito = inscripcion != null;
        boolean inscripcionActiva = inscripcion != null && "ACTIVO".equals(inscripcion.getEstado());
        boolean noHaFirmado = detalleExistente == null;
        boolean dentroHorario = sesion.getFechaInicio() == null || LocalDateTime.now().isAfter(sesion.getFechaInicio());
        boolean coordenadasValidas = true; // Se validará en el momento de firmar (radio de proximidad)
        
        List<String> restriccionesVioladas = new java.util.ArrayList<>();
        
        if (!sesionActiva) restriccionesVioladas.add("La sesión no está activa");
        if (!sesionDisponible) restriccionesVioladas.add("La sesión ya terminó");
        if (!estudianteInscrito) restriccionesVioladas.add("El estudiante no está inscrito en el curso");
        if (!inscripcionActiva) restriccionesVioladas.add("La inscripción del estudiante no está activa");
        if (!noHaFirmado) restriccionesVioladas.add("El estudiante ya firmó en esta sesión");
        if (!dentroHorario) restriccionesVioladas.add("La sesión aún no ha comenzado");
        
        return ValidacionAsistenciaResponse.Restricciones.builder()
            .sesionActiva(sesionActiva)
            .sesionDisponible(sesionDisponible)
            .estudianteInscrito(estudianteInscrito)
            .inscripcionActiva(inscripcionActiva)
            .noHaFirmado(noHaFirmado)
            .dentroHorario(dentroHorario)
            .coordenadasValidas(coordenadasValidas)
            .restriccionesVioladas(restriccionesVioladas)
            .build();
    }
    
    private ValidacionAsistenciaResponse.RecomendacionesValidacion generarRecomendacionesValidacion(
            Sesion sesion, Usuario estudiante, ValidacionAsistenciaResponse.Restricciones restricciones) {
        
        String recomendacionGeneral = generarRecomendacionGeneralValidacion(restricciones);
        String recomendacionTiempo = generarRecomendacionTiempoValidacion(sesion);
        String recomendacionUbicacion = generarRecomendacionUbicacionValidacion(sesion);
        
        List<String> accionesSugeridas = generarAccionesSugeridasValidacion(sesion, restricciones);
        String proximaOportunidad = calcularProximaOportunidad(sesion);
        
        return ValidacionAsistenciaResponse.RecomendacionesValidacion.builder()
            .recomendacionGeneral(recomendacionGeneral)
            .recomendacionTiempo(recomendacionTiempo)
            .recomendacionUbicacion(recomendacionUbicacion)
            .accionesSugeridas(accionesSugeridas)
            .proximaOportunidad(proximaOportunidad)
            .build();
    }
    
    private String generarRecomendacionGeneralValidacion(ValidacionAsistenciaResponse.Restricciones restricciones) {
        if (restricciones.getRestriccionesVioladas().isEmpty()) {
            return "Puedes firmar tu asistencia ahora";
        } else {
            return "No puedes firmar en este momento. Revisa las restricciones";
        }
    }
    
    private String generarRecomendacionTiempoValidacion(Sesion sesion) {
        if (sesion.getFechaInicio() != null && LocalDateTime.now().isBefore(sesion.getFechaInicio())) {
            long minutosRestantes = java.time.Duration.between(LocalDateTime.now(), sesion.getFechaInicio()).toMinutes();
            return String.format("La sesión comenzará en %d minutos", minutosRestantes);
        }
        return "El horario de la sesión es adecuado para firmar";
    }
    
    private String generarRecomendacionUbicacionValidacion(Sesion sesion) {
        return String.format("Asegúrate de estar dentro del radio de %.1f metros del aula", sesion.getRadioProximidad());
    }
    
    private List<String> generarAccionesSugeridasValidacion(Sesion sesion, ValidacionAsistenciaResponse.Restricciones restricciones) {
        List<String> acciones = new java.util.ArrayList<>();
        
        if (!restricciones.getSesionActiva()) {
            acciones.add("Esperar a que el profesor active la sesión");
        }
        
        if (!restricciones.getEstudianteInscrito()) {
            acciones.add("Contactar al profesor para inscribirse en el curso");
        }
        
        if (!restricciones.getNoHaFirmado()) {
            acciones.add("Ya has firmado en esta sesión");
        }
        
        if (restricciones.getRestriccionesVioladas().isEmpty()) {
            acciones.add("Proceder a firmar la asistencia");
            acciones.add("Verificar que estás dentro del radio permitido");
        }
        
        return acciones;
    }
    
    private String calcularProximaOportunidad(Sesion sesion) {
        if (sesion.getFechaInicio() != null && LocalDateTime.now().isBefore(sesion.getFechaInicio())) {
            return "La sesión comenzará en " + sesion.getFechaInicio().toString();
        }
        return "Puedes firmar ahora si cumples con todas las restricciones";
    }
    
    private String generarMensajeValidacion(boolean puedeFirmar, ValidacionAsistenciaResponse.Restricciones restricciones) {
        if (puedeFirmar) {
            return "Validación exitosa. Puedes proceder a firmar tu asistencia";
        } else {
            return "No puedes firmar en este momento. Restricciones: " + 
                   String.join(", ", restricciones.getRestriccionesVioladas());
        }
    }
}