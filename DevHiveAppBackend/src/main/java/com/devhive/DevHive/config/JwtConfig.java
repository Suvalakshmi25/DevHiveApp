package com.devhive.DevHive.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
@Getter
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @PostConstruct
    void validateSecret() {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET must contain at least 32 characters.");
        }
    }
}