package com.devhive.DevHive.service;

import com.devhive.DevHive.dto.auth.LoginRequest;
import com.devhive.DevHive.dto.auth.RegisterRequest;
import com.devhive.DevHive.entity.User;
import com.devhive.DevHive.exception.ResourceNotFoundException;
import com.devhive.DevHive.exception.UnauthorizedException;
import com.devhive.DevHive.respository.UserRepository;
import com.devhive.DevHive.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public void register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
    }
    public String login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        List<String> roles = Optional.ofNullable(user.getMemberships())
                .orElse(List.of())
                .stream()
                .map(m -> m.getRole().name())
                .toList();

        return jwtUtil.generateToken(user.getEmail(), roles);
    }
}
