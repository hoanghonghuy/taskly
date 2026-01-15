package io.github.hoanghonghuy.taskly.dto.auth;

import io.github.hoanghonghuy.taskly.dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String tokenType;
    private long expiresInSeconds;
    private String refreshToken;
    private UserResponse user;
}
