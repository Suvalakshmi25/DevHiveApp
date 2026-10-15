package com.devhive.DevHive.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devhive.DevHive.config.JwtConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);
    
    @Autowired
    private final JwtConfig jwtConfig;
    public String generateToken(String email, List<String> roles) {
        return Jwts.builder()
                .setSubject(email)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
                .signWith(
                            Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(java.nio.charset.StandardCharsets.UTF_8)),
                        SignatureAlgorithm.HS256
                )
                .compact();
    }

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public boolean validateToken(String token, String email) {
        try {
            String extractedEmail = extractEmail(token);
            boolean emailMatches = email.equals(extractedEmail);
            LOGGER.info("validateToken: email={}, extractedEmail={}, emailMatches={}", email, extractedEmail, emailMatches);
            
            boolean expired = isExpired(token);
            LOGGER.info("validateToken: token expired={}", expired);
            
            boolean valid = emailMatches && !expired;
            LOGGER.info("validateToken: final result={}", valid);
            return valid;
        } catch (Exception ex) {
            LOGGER.error("validateToken failed for email={}: {}", email, ex.getMessage(), ex);
            return false;
        }
    }

    private boolean isExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        try {
            LOGGER.debug("getClaims: parsing token, length={}", token.length());
            LOGGER.debug("getClaims: token starts with: {}", token.substring(0, Math.min(50, token.length())));
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            LOGGER.info("getClaims: successfully parsed token, subject={}, expiration={}", claims.getSubject(), claims.getExpiration());
            return claims;
        } catch (Exception ex) {
            LOGGER.error("getClaims: failed to parse token: {}", ex.getMessage(), ex);
            throw ex;
        }
    }
}