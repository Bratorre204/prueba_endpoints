package com.asistencia.backend.service;

import com.asistencia.backend.Auth.AuthResponse;
import com.asistencia.backend.Auth.LoginRequest;
import com.asistencia.backend.Auth.RegisterRequest;
import com.asistencia.backend.model.Rol;
import com.asistencia.backend.model.RolNombre;
import com.asistencia.backend.model.Usuario;
import com.asistencia.backend.repository.RolRepository;
import com.asistencia.backend.repository.UserRepository;
import com.asistencia.backend.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
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

            return AuthResponse.builder()
                    .token(token)
                    .nombre(usuario.getNombre())
                    .apellido(usuario.getApellido())
                    .correo(usuario.getCorreo())
                    .programa(usuario.getPrograma())
                    .build();

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Correo o contraseña inválidos");
        }
    }

    public AuthResponse register(RegisterRequest request) {

        RolNombre rolNombre = RolNombre.valueOf(request.getRol().toUpperCase());
        Rol rol = rolRepository.findByNombre(rolNombre)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + rolNombre));

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .correo(request.getCorreo())
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .programa(request.getPrograma())
                .build();

        // Agregar rol al usuario
        usuario.getRoles().add(rol);

        userRepository.save(usuario);

        return AuthResponse.builder()
                .token(jwtService.generateToken(usuario))
                .correo(usuario.getCorreo())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .programa(usuario.getPrograma())
                .build();
    }
}
