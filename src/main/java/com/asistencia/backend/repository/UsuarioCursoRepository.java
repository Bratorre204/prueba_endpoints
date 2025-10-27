package com.asistencia.backend.repository;

import com.asistencia.backend.model.UsuarioCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioCursoRepository extends JpaRepository<UsuarioCurso, Long> {
    
    @Query("SELECT uc FROM UsuarioCurso uc WHERE uc.curso.id = :idCurso")
    List<UsuarioCurso> findByCursoId(@Param("idCurso") Long idCurso);
    
    @Query("SELECT uc FROM UsuarioCurso uc WHERE uc.usuario.id = :idUsuario")
    List<UsuarioCurso> findByUsuarioId(@Param("idUsuario") Long idUsuario);
    
    @Query("SELECT uc FROM UsuarioCurso uc WHERE uc.usuario.id = :idUsuario AND uc.curso.id = :idCurso")
    UsuarioCurso findByUsuarioIdAndCursoId(@Param("idUsuario") Long idUsuario, @Param("idCurso") Long idCurso);
    
    @Query("SELECT uc FROM UsuarioCurso uc WHERE uc.usuario.id = :idUsuario AND uc.estado = 'ACTIVO'")
    List<UsuarioCurso> findByUsuarioIdAndEstadoActivo(@Param("idUsuario") Long idUsuario);
    
    @Query("SELECT uc FROM UsuarioCurso uc WHERE uc.curso.id = :idCurso AND uc.estado = 'ACTIVO'")
    List<UsuarioCurso> findByCursoIdAndEstadoActivo(@Param("idCurso") Long idCurso);
    
    @Query("SELECT COUNT(uc) FROM UsuarioCurso uc WHERE uc.curso.id = :idCurso AND uc.estado = 'ACTIVO'")
    Long countByCursoIdAndEstadoActivo(@Param("idCurso") Long idCurso);
    
    boolean existsByUsuarioIdAndCursoId(Long usuarioId, Long cursoId);
}
