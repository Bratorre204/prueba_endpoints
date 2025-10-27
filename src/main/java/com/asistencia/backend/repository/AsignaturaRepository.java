package com.asistencia.backend.repository;
import com.asistencia.backend.model.Asignatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AsignaturaRepository extends JpaRepository<Asignatura, Long> {

    Optional<Asignatura> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
}