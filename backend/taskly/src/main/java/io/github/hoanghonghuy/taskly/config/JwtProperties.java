package io.github.hoanghonghuy.taskly.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties( String secret, String issuer, int accessTokenMinutes) {
}
