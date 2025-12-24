package io.github.hoanghonghuy.taskly.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import io.github.hoanghonghuy.taskly.dto.auth.AuthResponse;
import io.github.hoanghonghuy.taskly.dto.auth.LoginRequest;
import io.github.hoanghonghuy.taskly.dto.auth.RegisterRequest;
import io.github.hoanghonghuy.taskly.dto.auth.TokenRefreshRequest;
import io.github.hoanghonghuy.taskly.dto.auth.TokenRefreshResponse;
import io.github.hoanghonghuy.taskly.entity.RefreshToken;
import io.github.hoanghonghuy.taskly.entity.User;
import io.github.hoanghonghuy.taskly.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, 
                    PasswordEncoder passwordEncoder, 
                    JwtService jwtService,
                    RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public void register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(email).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password")
        );
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        String accessToken = jwtService.createAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
        
        return new AuthResponse(accessToken, "Bearer", 60L * 60L, refreshToken.getToken());
    }

    @Transactional
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtService.createAccessToken(user);
                    return new TokenRefreshResponse(accessToken, requestRefreshToken);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Refresh token is not in database!"));
    }
}
