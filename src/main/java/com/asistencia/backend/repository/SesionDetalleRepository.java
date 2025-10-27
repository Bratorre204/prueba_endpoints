package com.asistencia.backend.repository;

import com.asistencia.backend.model.SesionDetalle;
import com.asistencia.backend.model.EstadoAsistencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SesionDetalleRepository extends JpaRepository<SesionDetalle, Long> {
    
    List<SesionDetalle> findBySesionId(Long sesionId);
    
    @Query("SELECT sd FROM SesionDetalle sd WHERE sd.estudiante.id = :idEstudiante")
    Page<SesionDetalle> findByEstudianteId(@Param("idEstudiante") Long idEstudiante, Pageable pageable);
    
    @Query("SELECT sd FROM SesionDetalle sd WHERE sd.estudiante.id = :idEstudiante AND sd.sesion.curso.id = :idCurso")
    Page<SesionDetalle> findByEstudianteIdAndCursoId(@Param("idEstudiante") Long idEstudiante, @Param("idCurso") Long idCurso, Pageable pageable);
    
    @Query("SELECT sd FROM SesionDetalle sd WHERE sd.estudiante.id = :idEstudiante AND sd.sesion.curso.periodo = :periodo")
    Page<SesionDetalle> findByEstudianteIdAndPeriodo(@Param("idEstudiante") Long idEstudiante, @Param("periodo") String periodo, Pageable pageable);
    
    @Query("SELECT sd FROM SesionDetalle sd WHERE sd.sesion.id = :idSesion AND sd.estado = :estado")
    List<SesionDetalle> findBySesionIdAndEstado(@Param("idSesion") Long idSesion, @Param("estado") EstadoAsistencia estado);
    
    @Query("SELECT COUNT(sd) FROM SesionDetalle sd WHERE sd.estudiante.id = :idEstudiante AND sd.estado = :estado")
    Long countByEstudianteIdAndEstado(@Param("idEstudiante") Long idEstudiante, @Param("estado") EstadoAsistencia estado);
    
    @Query("SELECT COUNT(sd) FROM SesionDetalle sd WHERE sd.estudiante.id = :idEstudiante AND sd.sesion.curso.id = :idCurso AND sd.estado = :estado")
    Long countByEstudianteIdAndCursoIdAndEstado(@Param("idEstudiante") Long idEstudiante, @Param("idCurso") Long idCurso, @Param("estado") EstadoAsistencia estado);
    
    @Query("SELECT sd FROM SesionDetalle sd WHERE sd.sesion.id = :idSesion AND sd.estudiante.id = :idEstudiante")
    SesionDetalle findBySesionIdAndEstudianteId(@Param("idSesion") Long idSesion, @Param("idEstudiante") Long idEstudiante);
    
    @Query("SELECT COUNT(sd) FROM SesionDetalle sd WHERE sd.sesion.id = :idSesion")
    Long countBySesionId(@Param("idSesion") Long idSesion);
    
    @Query("SELECT COUNT(sd) FROM SesionDetalle sd WHERE sd.sesion.id = :idSesion AND sd.estado = :estado")
    Long countBySesionIdAndEstado(@Param("idSesion") Long idSesion, @Param("estado") EstadoAsistencia estado);
}
