package com.asistencia.backend.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaService {

    public String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "SISTEMA"; // Usuario por defecto si no hay autenticación
        }

        String username = authentication.getName();
        return (username != null && !username.isBlank()) ? username : "SISTEMA";
    }
}
