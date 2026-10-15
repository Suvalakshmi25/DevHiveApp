package com.devhive.DevHive.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.devhive.DevHive.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

// ✅ VERY IMPORTANT: skip auth endpoints
        if (path.startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        String header = request.getHeader("Authorization");
        LOGGER.debug("JwtFilter processing request {}, Authorization header present={}", path, header != null);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7).trim();
            if (token.isEmpty()) {
                throw new BadCredentialsException("Bearer token is missing");
            }

            try {
                String email = jwtUtil.extractEmail(token);
                boolean valid = false;
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (email != null && (authentication == null
                        || authentication instanceof AnonymousAuthenticationToken)) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    valid = jwtUtil.validateToken(token, email);
                    if (valid) {
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());

                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
                LOGGER.info("JwtFilter token email={}, valid={}", email, valid);
                if (!valid) {
                    throw new BadCredentialsException("Invalid or expired bearer token");
                }
            } catch (RuntimeException ex) {
                LOGGER.warn("JWT authentication failed: {}", ex.getMessage());
                throw new BadCredentialsException("Invalid or expired bearer token", ex);
            }
        } else if (header != null) {
            throw new BadCredentialsException("Authorization header must start with 'Bearer '");
        }

        filterChain.doFilter(request, response);
    }

}