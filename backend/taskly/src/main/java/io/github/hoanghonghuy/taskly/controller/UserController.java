package io.github.hoanghonghuy.taskly.controller;

import java.util.Map;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(JwtAuthenticationToken auth) {
        return Map.of(
            "userId", auth.getName(),
            "email", auth.getToken().getClaimAsString("email")
        );
    }
}
