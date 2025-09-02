package com.asistencia.backend.Auth;

import com.asistencia.backend.model.Role;
import com.asistencia.backend.repository.AuditoriaRepository;
import com.asistencia.backend.repository.UserRepository;
import com.asistencia.backend.model.Usuario;
import com.asistencia.backend.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.asistencia.backend.jwt.JwtService;
import org.springframework.security.authentication.BadCredentialsException;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private  final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuditoriaService auditoriaService;


    public AuthResponse login(LoginRequest request) {
        String latitud = request.getLatitud();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getCorreo(),
                            request.getContrasena()
                    )
            );

            Usuario usuario = userRepository.findByCorreo(request.getCorreo())
                    .orElseThrow();

            String token = jwtService.generateToken(usuario);
            auditoriaService.auditSuccessfulLogin(latitud, usuario);

            return AuthResponse.builder()
                    .token(token)
                    .nombre(usuario.getNombre())
                    .apellido(usuario.getApellido())
                    .correo(usuario.getCorreo())
                    .programa(usuario.getPrograma())
                    .build();
        } catch (BadCredentialsException e) {
            auditoriaService.auditFailedLogin(latitud);
            throw new BadCredentialsException("Correo o contraseña inválidos");
        }
    }


    public AuthResponse register(RegisterRequest request) {
        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .correo(request.getCorreo())
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .programa(request.getPrograma())
                .rol(Role.USER)
                .build();

        userRepository.save(usuario);
        return AuthResponse.builder()
                .token(jwtService.generateToken(usuario))
                .correo(usuario.getCorreo())
                .build();

    }
}
