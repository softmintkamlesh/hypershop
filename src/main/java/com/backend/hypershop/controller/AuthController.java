package com.backend.hypershop.controller;

import com.backend.hypershop.dto.request.OtpRequest;
import com.backend.hypershop.dto.request.OtpVerifyRequest;
import com.backend.hypershop.dto.schema.GlobalResponse;
import com.backend.hypershop.repository.UserOtpRepository;
import com.backend.hypershop.repository.UserRepository;
import com.backend.hypershop.service.AuthService;
import com.backend.hypershop.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final UserOtpRepository userOtpRepository;
    private final JwtUtil jwtUtil;


    @PostMapping("/consumer/login/requestOtp")
    public GlobalResponse<?> requestLoginOtp(@Valid @RequestBody OtpRequest request) {
       return authService.sendConsumerLoginOtp(request);
    }

    @PostMapping("/consumer/login/verifyOtp")
    public GlobalResponse<?> verifyOtpAndLogin(@Valid @RequestBody OtpVerifyRequest request) {
       return authService.verifyConsumerLoginOtp(request);
    }



}
