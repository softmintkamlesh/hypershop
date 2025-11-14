// OtpServiceImpl.java
package com.backend.hypershop.service.impl;

import com.backend.hypershop.entity.UserOtp;
import com.backend.hypershop.exception.InvalidOperationException;
import com.backend.hypershop.repository.UserOtpRepository;
import com.backend.hypershop.service.OtpService;
//import com.backend.hypershop.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {

    private final UserOtpRepository userOtpRepository;
//    private final SmsService smsService;
    
    private static final int OTP_LENGTH = 6;
    private static final int OTP_VALIDITY_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 3;
    private static final int RATE_LIMIT_SECONDS = 60;

    @Override
    @Transactional
    public String generateAndSendOtp(String mobile) {
        return generateAndSendOtp(mobile, "");
    }

    @Override
    @Transactional
    public String generateAndSendOtp(String mobile, String userId) {
        Optional<UserOtp> existingOtp = userOtpRepository.findByMobile(mobile);

        if (existingOtp.isPresent()) {
            UserOtp otp = existingOtp.get();
            
            // Rate limiting check
            if (otp.getCreatedAt().plusSeconds(RATE_LIMIT_SECONDS).isAfter(LocalDateTime.now())) {
                long secondsLeft = RATE_LIMIT_SECONDS - 
                    java.time.Duration.between(otp.getCreatedAt(), LocalDateTime.now()).getSeconds();
                throw new InvalidOperationException(
                    String.format("Please wait %d seconds before requesting a new OTP", secondsLeft)
                );
            }

            // Update existing record
            String newOtp = generateSecureOtp();
            log.debug("Updating OTP for mobile: {}", maskMobile(mobile));

            otp.setOtp(newOtp);
            otp.setUserId(userId);
            otp.setCreatedAt(LocalDateTime.now());
            otp.setExpiredAt(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES));
            otp.setAttemptCount(0);
            
            userOtpRepository.save(otp);
//            smsService.sendOtp(mobile, newOtp);
            
            return "New OTP sent successfully to " + maskMobile(mobile);
        }

        // Create new record
        String otp = generateSecureOtp();
        log.debug("Creating new OTP for mobile: {}", maskMobile(mobile));

        UserOtp userOtp = UserOtp.builder()
                .userId(userId)
                .mobile(mobile)
                .otp(otp)
                .attemptCount(0)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES))
                .build();

        userOtpRepository.save(userOtp);
//        smsService.sendOtp(mobile, otp);

        return "OTP sent successfully to " + maskMobile(mobile);
    }

    @Override
    @Transactional
    public boolean verifyOtp(String mobile, String otp) {
        UserOtp userOtp = userOtpRepository.findByMobile(mobile)
                .orElseThrow(() -> new InvalidOperationException(
                    "No OTP found for this mobile number. Please request a new OTP"
                ));

        if (userOtp.isExpired()) {
            userOtpRepository.delete(userOtp);
            throw new InvalidOperationException("OTP has expired. Please request a new one");
        }

        if (userOtp.isMaxAttemptsReached()) {
            userOtpRepository.delete(userOtp);
            throw new InvalidOperationException(
                "Maximum attempts exceeded. Please request a new OTP"
            );
        }

        userOtp.incrementAttempt();
        userOtpRepository.save(userOtp);

        if (!userOtp.getOtp().equals(otp)) {
            throw new InvalidOperationException(
                String.format("Invalid OTP. %d attempt(s) remaining", 
                    MAX_ATTEMPTS - userOtp.getAttemptCount())
            );
        }

        userOtpRepository.delete(userOtp);
        log.info("OTP verified successfully for mobile: {}", maskMobile(mobile));

        return true;
    }

    @Override
    public String getUserIdFromOtp(String mobile) {
        return userOtpRepository.findByMobile(mobile)
                .map(UserOtp::getUserId)
                .orElse("");
    }

    @Override
    @Transactional
    public String resendOtp(String mobile) {
        UserOtp existingOtp = userOtpRepository.findByMobile(mobile)
                .orElseThrow(() -> new InvalidOperationException(
                    "No OTP session found. Please request a new OTP"
                ));

        userOtpRepository.delete(existingOtp);
        return generateAndSendOtp(mobile, existingOtp.getUserId());
    }



    private String generateSecureOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private String maskMobile(String mobile) {
        if (mobile.length() >= 10) {
            return mobile.substring(0, 2) + "******" + mobile.substring(8);
        }
        return "******";
    }
}
