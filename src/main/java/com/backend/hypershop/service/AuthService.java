package com.backend.hypershop.service;

import com.backend.hypershop.dto.request.LoginRequest;
import com.backend.hypershop.dto.request.RegisterRequest;
import com.backend.hypershop.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
