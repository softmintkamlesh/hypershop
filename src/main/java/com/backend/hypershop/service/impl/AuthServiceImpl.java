package com.backend.hypershop.service.impl;

import com.backend.hypershop.constants.Role;
import com.backend.hypershop.dto.request.LoginRequest;
import com.backend.hypershop.dto.request.RegisterRequest;
import com.backend.hypershop.dto.response.AuthResponse;
import com.backend.hypershop.entity.User;
import com.backend.hypershop.repository.UserRepository;
import com.backend.hypershop.service.AuthService;
import com.backend.hypershop.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.findByMobile(request.getPhone()).isPresent()) {
            throw new RuntimeException("Phone already registered");
        }

        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setMobile(request.getPhone());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CONSUMER);

        user = userRepository.save(user);

        // Generate token
        String token = jwtUtil.generateToken(user.getUserId(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getUserId())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByMobile(request.getPhone())
                .orElseThrow(() -> new RuntimeException("Invalid mobile no"));


        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Generate token
        String token = jwtUtil.generateToken(user.getUserId(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getUserId())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }
}
