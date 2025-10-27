package com.asistencia.backend.Config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        // Log the error for debugging
        System.out.println("Access denied for URI: " + request.getRequestURI());
        System.out.println("Method: " + request.getMethod());
        System.out.println("Exception: " + accessDeniedException.getMessage());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"No tienes permisos para acceder a este recurso\", \"uri\": \"" + request.getRequestURI() + "\", \"method\": \"" + request.getMethod() + "\"}");
    }
}
