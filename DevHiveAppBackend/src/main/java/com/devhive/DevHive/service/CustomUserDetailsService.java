package com.devhive.DevHive.service;

import com.devhive.DevHive.entity.User;
import com.devhive.DevHive.respository.UserRepository;
import com.devhive.DevHive.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserPrincipal loadUserByUsername(String email) {
        User user = userRepository.findByEmailWithMemberships(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserPrincipal(user);
    }
}