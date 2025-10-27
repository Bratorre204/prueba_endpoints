package com.asistencia.backend.Auth;

import com.asistencia.backend.service.AuthService;
import com.asistencia.backend.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody @Valid LoginRequest request) {
        try {
            AuthResponse data = authService.login(request);
            ApiResponse response = ApiResponse.builder()
                    .success(true)
                    .message("Login exitoso!")
                    .data(data)
                    .build();
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            ApiResponse errorResponse = ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid RegisterRequest request) {
        try {
            AuthResponse data = authService.register(request);
            ApiResponse response = ApiResponse.builder()
                    .success(true)
                    .message("Registro exitoso!")
                    .data(data)
                    .build();
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ApiResponse errorResponse = ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
