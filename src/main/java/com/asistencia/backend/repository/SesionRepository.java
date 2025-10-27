package com.asistencia.backend.repository;

import com.asistencia.backend.model.Sesion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SesionRepository extends JpaRepository<Sesion, Long> {
    
    @Query("SELECT s FROM Sesion s WHERE s.profesor.id = :idProfesor")
    Page<Sesion> findByProfesorId(@Param("idProfesor") Long idProfesor, Pageable pageable);
    
    @Query("SELECT s FROM Sesion s WHERE s.profesor.id = :idProfesor AND s.curso.periodo = :periodo")
    Page<Sesion> findByProfesorIdAndPeriodo(@Param("idProfesor") Long idProfesor, @Param("periodo") String periodo, Pageable pageable);
    
    @Query("SELECT s FROM Sesion s WHERE s.curso.id = :idCurso AND s.estado = 'ACTIVA'")
    List<Sesion> findActivasByCursoId(@Param("idCurso") Long idCurso);
    
    @Query("SELECT s FROM Sesion s WHERE s.curso.id = :idCurso")
    List<Sesion> findByCursoId(@Param("idCurso") Long idCurso);
    
    @Query("SELECT s FROM Sesion s WHERE s.estado = 'ACTIVA' AND s.fechaCreacion >= :fechaInicio")
    List<Sesion> findActivasDesdeFecha(@Param("fechaInicio") LocalDateTime fechaInicio);
    
    @Query("SELECT COUNT(s) FROM Sesion s WHERE s.curso.id = :idCurso")
    Long countByCursoId(@Param("idCurso") Long idCurso);
    
    @Query("SELECT COUNT(s) FROM Sesion s WHERE s.profesor.id = :idProfesor AND s.estado = 'ACTIVA'")
    Long countActivasByProfesorId(@Param("idProfesor") Long idProfesor);
    
    // Validar si ya existe una sesión activa para el curso en el mismo día
    @Query("SELECT COUNT(s) FROM Sesion s WHERE s.curso.id = :idCurso AND s.estado = 'ACTIVA' AND DATE(s.fechaInicio) = DATE(:fechaInicio)")
    Long countActivasByCursoIdAndFecha(@Param("idCurso") Long idCurso, @Param("fechaInicio") LocalDateTime fechaInicio);
    
    // Buscar sesiones activas por curso y fecha
    @Query("SELECT s FROM Sesion s WHERE s.curso.id = :idCurso AND s.estado = 'ACTIVA' AND DATE(s.fechaInicio) = DATE(:fechaInicio)")
    List<Sesion> findActivasByCursoIdAndFecha(@Param("idCurso") Long idCurso, @Param("fechaInicio") LocalDateTime fechaInicio);
}
