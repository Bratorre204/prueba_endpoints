package com.asistencia.backend.Config;

import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null &&
                authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser") &&
                authentication.getName() != null) {
            return Optional.of(authentication.getName());
        }
        return Optional.of("SISTEMA"); // Usuario por defecto si no hay autenticación
    }
}

