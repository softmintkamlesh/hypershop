// OtpService.java
package com.backend.hypershop.service;

public interface OtpService {

    /**
     * Generate and send OTP to mobile
     */
    String generateAndSendOtp(String mobile);

    /**
     * Generate and send OTP to mobile with userId
     */
    String generateAndSendOtp(String mobile, String userId);

    /**
     * Verify OTP
     */
    boolean verifyOtp(String mobile, String otp);

    /**
     * Resend OTP
     */
    String resendOtp(String mobile);

    /**
     * Get user ID from OTP session
     */
    String getUserIdFromOtp(String mobile);


}
