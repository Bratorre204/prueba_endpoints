package com.asistencia.backend.repository;
import com.asistencia.backend.model.AuditoriaLogin;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaRepository extends JpaRepository<AuditoriaLogin, Long> {
}
