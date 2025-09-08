package com.Quiz.user_service.service;

import com.Quiz.user_service.config.JwtUtil;
import com.Quiz.user_service.dto.JwtResponse;
import com.Quiz.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Optional<JwtResponse> login(String username, String password) {
        return userRepository.getUserByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> new JwtResponse(jwtUtil.generateToken(user.getUsername())));
    }
}
