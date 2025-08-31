package com.asistencia.backend.Auth;

import com.asistencia.backend.User.Role;
import com.asistencia.backend.User.UserRepository;
import com.asistencia.backend.User.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.asistencia.backend.jwt.JwtService;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private  final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Usuario usuario = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        String token = jwtService.generateToken(usuario);

        return AuthResponse.builder()
                .token(token)
                .lastName(usuario.getLastName())
                .firstName(usuario.getFirstName())
                .email(usuario.getEmail())
                .carrera(usuario.getCarrera())
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        Usuario usuario = Usuario.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .carrera(request.getCarrera())
                .role(Role.USER)
                .build();

        userRepository.save(usuario);
        return AuthResponse.builder()
                .token(jwtService.generateToken(usuario))
                .email(usuario.getEmail())
                .build();

    }
}
