package com.asistencia.backend.repository;

import com.asistencia.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Usuario, Long> {

    // Método original
    Optional<Usuario> findByCorreo(String correo);

    // Método para buscar usuario activo por correo (no eliminado lógicamente)
    @Query("SELECT u FROM Usuario u WHERE u.correo = :correo AND u.deleteLogic = false")
    Optional<Usuario> findByCorreoAndDeleteLogicFalse(@Param("correo") String correo);

    // Método alternativo usando nomenclatura Spring Data JPA
    // Optional<Usuario> findByCorreoAndDeleteLogicFalse(String correo);

    // Métodos adicionales útiles para el sistema

    // Buscar todos los usuarios activos
    @Query("SELECT u FROM Usuario u WHERE u.deleteLogic = false")
    java.util.List<Usuario> findAllActive();

    // Buscar usuario por ID y que esté activo
    @Query("SELECT u FROM Usuario u WHERE u.id = :id AND u.deleteLogic = false")
    Optional<Usuario> findByIdAndActive(@Param("id") Long id);

    // Verificar si existe un correo (para registro)
    boolean existsByCorreo(String correo);

    // Verificar si existe un correo activo (no eliminado)
    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.correo = :correo AND u.deleteLogic = false")
    boolean existsByCorreoAndActive(@Param("correo") String correo);

    // Buscar usuarios por rol (si tienes relación con roles)
    @Query("SELECT u FROM Usuario u JOIN u.roles r WHERE r.nombre = :nombreRol AND u.deleteLogic = false")
    java.util.List<Usuario> findByRoleName(@Param("nombreRol") com.asistencia.backend.model.RolNombre nombreRol);
}