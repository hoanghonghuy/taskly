package io.github.hoanghonghuy.taskly.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.hoanghonghuy.taskly.dto.auth.AuthResponse;
import io.github.hoanghonghuy.taskly.dto.auth.LoginRequest;
import io.github.hoanghonghuy.taskly.dto.auth.RegisterRequest;
import io.github.hoanghonghuy.taskly.dto.auth.TokenRefreshRequest;
import io.github.hoanghonghuy.taskly.dto.auth.TokenRefreshResponse;
import io.github.hoanghonghuy.taskly.service.AuthService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {
    private final AuthService authService;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh-token")
    public TokenRefreshResponse refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return authService.refreshToken(request);
    }
}
