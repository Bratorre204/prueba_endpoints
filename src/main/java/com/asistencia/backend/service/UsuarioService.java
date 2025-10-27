package com.asistencia.backend.service;

import com.asistencia.backend.dto.*;
import com.asistencia.backend.model.*;
import com.asistencia.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {
    
    private final UserRepository usuarioRepository;
    
    public Usuario getUsuarioById(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    
    public List<UsuarioDTO> buscarUsuario(String query) {
        List<Usuario> usuarios = usuarioRepository.findAll().stream()
            .filter(u -> u.getNombre().toLowerCase().contains(query.toLowerCase()) ||
                        u.getApellido().toLowerCase().contains(query.toLowerCase()) ||
                        u.getCorreo().toLowerCase().contains(query.toLowerCase()))
            .collect(Collectors.toList());
        
        return usuarios.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    public Usuario actualizarUsuario(Long id, ActualizarUsuarioRequest request) {
        Usuario usuario = getUsuarioById(id);
        
        if (request.getNombre() != null) {
            usuario.setNombre(request.getNombre());
        }
        if (request.getApellido() != null) {
            usuario.setApellido(request.getApellido());
        }
        if (request.getCorreo() != null) {
            usuario.setCorreo(request.getCorreo());
        }
        if (request.getPrograma() != null) {
            usuario.setPrograma(request.getPrograma());
        }
        
        return usuarioRepository.save(usuario);
    }
    
    private UsuarioDTO convertirADTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
            .identificacion(usuario.getCorreo()) // Usando correo como identificación
            .nombre(usuario.getNombre())
            .email(usuario.getCorreo())
            .build();
    }
}
