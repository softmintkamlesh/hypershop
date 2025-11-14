package com.backend.hypershop.service.impl;

import com.backend.hypershop.constants.Role;
import com.backend.hypershop.dto.request.*;
import com.backend.hypershop.dto.response.AuthResponse;
import com.backend.hypershop.dto.schema.GlobalResponse;
import com.backend.hypershop.entity.User;
import com.backend.hypershop.entity.UserOtp;
import com.backend.hypershop.exception.InvalidOperationException;
import com.backend.hypershop.exception.ResourceNotFoundException;
import com.backend.hypershop.repository.UserOtpRepository;
import com.backend.hypershop.repository.UserRepository;
import com.backend.hypershop.service.AuthService;
import com.backend.hypershop.service.OtpService;
import com.backend.hypershop.utils.AppUtil;
import com.backend.hypershop.utils.GeneratorUtil;
import com.backend.hypershop.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserOtpRepository userOtpRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    @Override
    public GlobalResponse<?> sendConsumerLoginOtp(OtpRequest request) {
        return sendOtp(request.getMobile(), Role.CONSUMER);
    }

    @Override
    public GlobalResponse<?> verifyConsumerLoginOtp(OtpVerifyRequest request) {
        String mobile = request.getMobile();
        String otp = request.getOtp();
        log.info("OTP verification requested for mobile: {}", mobile);

        // Step 1: Find OTP record
        UserOtp userOtp = userOtpRepository.findByMobile(mobile)
                .orElseThrow(() -> new InvalidOperationException(
                        "No OTP found. Please request a new OTP"
                ));

        // Step 2: Check if OTP is active
        if (!userOtp.isStatus()) {
            throw new InvalidOperationException("OTP is no longer active. Please request a new one");
        }

        // Step 3: Check if OTP is expired
        if (userOtp.getExpiredAt().isBefore(LocalDateTime.now())) {
            // ✅ Mark as inactive
            userOtp.setStatus(false);
            userOtpRepository.save(userOtp);
            throw new InvalidOperationException("OTP has expired. Please request a new one");
        }

        // Step 4: Check max attempts
        if (userOtp.getAttemptCount() >= 3) {
            // ✅ Mark as inactive
            userOtp.setStatus(false);
            userOtpRepository.save(userOtp);
            throw new InvalidOperationException(
                    "Maximum attempts exceeded. Please request a new OTP"
            );
        }

        // Step 5: Increment attempt count
        userOtp.setAttemptCount(userOtp.getAttemptCount() + 1);
        userOtpRepository.save(userOtp);

        // Step 6: Verify OTP
        if (!userOtp.getOtp().equals(otp)) {
            int attemptsLeft = 3 - userOtp.getAttemptCount();
            throw new InvalidOperationException(
                    String.format("Invalid OTP. %d attempt(s) remaining", attemptsLeft)
            );
        }

        // Step 7: OTP verified successfully - ✅ Mark as inactive (used)
        userOtp.setStatus(false);
        userOtpRepository.save(userOtp);
        log.info("OTP verified successfully for mobile: {}", mobile);

        // Step 8: Get user details
        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Step 9: Generate JWT token
        String token = jwtUtil.generateToken(user.getUserId(), user.getRole().name());

        // Step 10: Build response
        AuthResponse response = AuthResponse.builder()
                .token(token)
                .userId(user.getUserId())
                .name(user.getName())
                .mobile(user.getMobile())
                .role(user.getRole().name())
                .build();

        log.info("Consumer logged in successfully: {}", user.getUserId());

        return GlobalResponse.success("Login successful", response);
    }

    @Override
    public GlobalResponse<?> sendRiderLoginOtp(OtpRequest request) {
        return sendOtp(request.getMobile(), Role.RIDER);
    }

    @Override
    public GlobalResponse<?> sendManagerLoginOtp(OtpRequest request) {
        return sendOtp(request.getMobile(), Role.DARK_STORE_MANAGER);
    }

    @Override
    public GlobalResponse<?> sendAdminLoginOtp(OtpRequest request) {
        return sendOtp(request.getMobile(), Role.ADMIN);
    }



    private GlobalResponse<Object> sendOtp(String mobile, Role expectedRole) {
        log.info("OTP login requested for mobile: {} with role: {}", mobile, expectedRole);

        // Step 1: Check if user exists
        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found. Please register first"
                ));

        // Step 2: Validate user role
        if (user.getRole() != expectedRole) {
            throw new InvalidOperationException(
                    String.format("Invalid user role. Expected: %s, Found: %s",
                            expectedRole, user.getRole())
            );
        }

        // Step 3: Check if OTP record exists for this mobile
        Optional<UserOtp> existingOtp = userOtpRepository.findByMobile(mobile);

        if (existingOtp.isPresent()) {
            // ✅ Update existing row
            UserOtp otp = existingOtp.get();

            // Rate limiting: Check if last OTP was created within 60 seconds
            long secondsSinceLastOtp = java.time.Duration.between(
                    otp.getCreatedAt(),
                    LocalDateTime.now()
            ).getSeconds();

            if (secondsSinceLastOtp < 60) {
                long secondsLeft = 60 - secondsSinceLastOtp;
                throw new InvalidOperationException(
                        String.format("Please wait %d seconds before requesting a new OTP", secondsLeft)
                );
            }

            // ✅ Update same row with new OTP
            String newOtp = GeneratorUtil.generateOtp();
            log.info("Generated new OTP for {}: {}", AppUtil.maskMobile(mobile), newOtp);

            otp.setOtp(newOtp);
            otp.setUserId(user.getUserId());
            otp.setCreatedAt(LocalDateTime.now());
            otp.setExpiredAt(LocalDateTime.now().plusMinutes(5));
            otp.setAttemptCount(0);
            otp.setStatus(true);  // ✅ Reactivate
            userOtpRepository.save(otp);

            // TODO: Send SMS here
            log.info("OTP sent via SMS: {}", newOtp);

            return GlobalResponse.success(
                    "New OTP sent successfully to " + AppUtil.maskMobile(mobile)
            );
        }

        // Step 4: ✅ Create new row (first time for this mobile)
        String otp = GeneratorUtil.generateOtp();
        log.info("Generated OTP for {}: {}", AppUtil.maskMobile(mobile), otp);

        UserOtp userOtp = UserOtp.builder()
                .userId(user.getUserId())
                .mobile(mobile)
                .otp(otp)
                .attemptCount(0)
                .status(true)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .build();

        userOtpRepository.save(userOtp);

        // TODO: Send SMS here
        log.info("OTP sent via SMS: {}", otp);

        return GlobalResponse.success(
                "OTP sent successfully to " + AppUtil.maskMobile(mobile)
        );
    }


}
