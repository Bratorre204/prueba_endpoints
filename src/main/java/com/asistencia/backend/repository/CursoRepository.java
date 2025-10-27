package com.asistencia.backend.repository;

import com.asistencia.backend.model.Curso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    
    // Buscar por código único
    Optional<Curso> findByCodigo(String codigo);
    
    // Verificar si existe un código
    boolean existsByCodigo(String codigo);
    
    // Buscar por año y semestre
    List<Curso> findByAñoAndSemestre(Integer año, Integer semestre);
    
    // Buscar por turno, año y semestre
    List<Curso> findByTurnoAndAñoAndSemestre(String turno, Integer año, Integer semestre);
    
    @Query("SELECT c FROM Curso c WHERE c.profesor.id = :idProfesor")
    List<Curso> findByProfesorId(@Param("idProfesor") Long idProfesor);
    
    @Query("SELECT c FROM Curso c WHERE c.profesor.id = :idProfesor AND c.periodo = :periodo")
    List<Curso> findByProfesorIdAndPeriodo(@Param("idProfesor") Long idProfesor, @Param("periodo") String periodo);
    
    @Query("SELECT c FROM Curso c JOIN c.estudiantes uc WHERE uc.usuario.id = :idEstudiante")
    List<Curso> findByEstudianteId(@Param("idEstudiante") Long idEstudiante);
    
    @Query("SELECT c FROM Curso c JOIN c.estudiantes uc WHERE uc.usuario.id = :idEstudiante AND c.periodo = :periodo")
    List<Curso> findByEstudianteIdAndPeriodo(@Param("idEstudiante") Long idEstudiante, @Param("periodo") String periodo);
    
    @Query("SELECT c FROM Curso c WHERE (:periodo IS NULL OR c.periodo = :periodo) AND (:turno IS NULL OR c.turno = :turno)")
    Page<Curso> findAllWithFilters(@Param("periodo") String periodo, @Param("turno") String turno, Pageable pageable);
    
    @Query("SELECT DISTINCT c.periodo FROM Curso c ORDER BY c.periodo DESC")
    List<String> findDistinctPeriodos();
    
    @Query("SELECT DISTINCT c.turno FROM Curso c ORDER BY c.turno")
    List<String> findDistinctTurnos();
    
    @Query("SELECT c FROM Curso c WHERE c.asignatura.id = :idAsignatura")
    List<Curso> findByAsignaturaId(@Param("idAsignatura") Long idAsignatura);
    
    @Query("SELECT c FROM Curso c WHERE c.asignatura.id = :idAsignatura AND c.periodo = :periodo")
    List<Curso> findByAsignaturaIdAndPeriodo(@Param("idAsignatura") Long idAsignatura, @Param("periodo") String periodo);
}
