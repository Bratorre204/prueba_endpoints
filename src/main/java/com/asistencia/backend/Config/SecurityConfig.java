package com.asistencia.backend.Config;

import com.asistencia.backend.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authProvider;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authRequest -> authRequest
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/sesiones/**").permitAll()
                        .requestMatchers("/api/asistencia/**").permitAll()
                        .requestMatchers("/api/cursos/**").permitAll()
                        .requestMatchers("/api/asignaturas/**").permitAll()
                        .requestMatchers("/api/reportes/**").permitAll()
                        .requestMatchers("/api/usuarios/**").permitAll()
                        .requestMatchers("/api/estudiantes/**").hasRole("ESTUDIANTE")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception ->
                        exception.accessDeniedHandler(accessDeniedHandler)
                )
                .sessionManagement(sessionManager ->
                        sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
