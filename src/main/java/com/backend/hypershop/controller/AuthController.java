package com.backend.hypershop.controller;

import com.backend.hypershop.dto.request.LoginRequest;
import com.backend.hypershop.dto.request.RegisterRequest;
import com.backend.hypershop.dto.response.AuthResponse;
import com.backend.hypershop.dto.schema.GlobalResponse;
import com.backend.hypershop.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public GlobalResponse<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return GlobalResponse.success("Registration successful", response);
    }

    @PostMapping("/login")
    public GlobalResponse<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return GlobalResponse.success("Login successful", response);
    }
}
