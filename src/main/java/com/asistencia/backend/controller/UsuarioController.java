package com.asistencia.backend.controller;

import com.asistencia.backend.dto.*;
import com.asistencia.backend.model.Usuario;
import com.asistencia.backend.response.ApiResponse;
import com.asistencia.backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    /**
     * GET /api/usuarios/{id}
     * Obtener perfil de usuario
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuario(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.getUsuarioById(id);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET /api/usuarios/buscar
     * Buscar usuarios por identificación o email
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarUsuario(@RequestParam String query) {
        try {
            List<UsuarioDTO> usuarios = usuarioService.buscarUsuario(query);
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * PUT /api/usuarios/{id}
     * Actualizar perfil de usuario
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(
            @PathVariable Long id,
            @RequestBody @Valid ActualizarUsuarioRequest request) {
        try {
            Usuario usuario = usuarioService.actualizarUsuario(id, request);
            return ResponseEntity.ok(new ApiResponse(true, "Usuario actualizado", usuario));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}
