package com.asistencia.backend.service;

import com.asistencia.backend.model.AuditoriaLogin;
import com.asistencia.backend.model.Usuario;
import com.asistencia.backend.repository.AuditoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditoriaService {
    private final AuditoriaRepository auditoriaRepository;


    public void auditSuccessfulLogin (String latitud, Usuario usuario) {
        AuditoriaLogin auditoriaLogin = AuditoriaLogin.builder()
                .id_usuario(usuario.getId_usuario())
                .fecha_hora(LocalDateTime.now())
                .exitoso(true)
                .latitud(latitud)
                .build();

        auditoriaRepository.save(auditoriaLogin);
    }

    public void auditFailedLogin (String latitud) {
        AuditoriaLogin auditoriaLogin = AuditoriaLogin.builder()
                .fecha_hora(LocalDateTime.now())
                .exitoso(false)
                .latitud(latitud)
                .build();
        auditoriaRepository.save(auditoriaLogin);
    }

}
