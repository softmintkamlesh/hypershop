package com.backend.hypershop.dto.request;


import lombok.Data;

@Data
public class OtpVerifyRequest {
    private String mobile;  // 10 digit
    private String otp;     // 6 digit
}