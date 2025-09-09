package com.asistencia.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/profesores")
@RequiredArgsConstructor

public class DocenteController {

    //saber si accede por docente
    @GetMapping("/crear-curso")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<String> pruebaDocente(Authentication authentication) {
        String nombreUsuario = authentication.getName();
        return ResponseEntity.ok("Bienvenido, crear curso " + nombreUsuario + ". Autorizacion verificada");
    }

}
