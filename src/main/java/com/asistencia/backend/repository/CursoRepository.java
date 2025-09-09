package com.asistencia.backend.repository;

import com.asistencia.backend.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    // Buscar cursos activos (no eliminados lógicamente)
    @Query("SELECT c FROM Curso c WHERE c.deleteLogic = false")
    List<Curso> findAllActive();

    // Buscar cursos por profesor
    @Query("SELECT c FROM Curso c WHERE c.profesor.id_usuario = :profesorId AND c.deleteLogic = false")
    List<Curso> findByProfesorId(@Param("profesorId") Long profesorId);

    // Buscar curso por ID y activo
    @Query("SELECT c FROM Curso c WHERE c.id_curso = :id AND c.deleteLogic = false")
    Optional<Curso> findByIdAndActive(@Param("id") Long id);

    // Buscar cursos por nombre (búsqueda parcial)
    @Query("SELECT c FROM Curso c WHERE c.nombre_curso ILIKE %:nombre% AND c.deleteLogic = false")
    List<Curso> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);
}
