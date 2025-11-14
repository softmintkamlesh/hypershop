// OtpResponse.java
package com.backend.hypershop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpResponse {
    
    private String message;
    private String phone;
    
    // Optional: Additional metadata
    private Integer expiryInSeconds;  // OTP validity duration
    private Boolean resendAvailable;  // Can user resend OTP?
    
    // Constructor for simple response
    public OtpResponse(String message, String phone) {
        this.message = message;
        this.phone = phone;
        this.expiryInSeconds = 300; // 5 minutes default
        this.resendAvailable = true;
    }
}
