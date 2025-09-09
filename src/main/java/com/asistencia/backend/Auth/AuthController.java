package com.asistencia.backend.Auth;

import com.asistencia.backend.service.AuthService;
import com.asistencia.backend.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse data = authService.login(request);
            ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
                    .status(true)
                    .message("Login exitoso!")
                    .data(data)
                    .build();
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            ApiResponse<AuthResponse> errorResponse = ApiResponse.<AuthResponse>builder()
                    .status(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @PostMapping("register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {
        AuthResponse data = authService.register(request);
        ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
                .status(true)
                .message("Registro exitoso!")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }
}
