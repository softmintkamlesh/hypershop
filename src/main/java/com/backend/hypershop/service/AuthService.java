package com.backend.hypershop.service;

import com.backend.hypershop.dto.request.OtpRequest;
import com.backend.hypershop.dto.request.OtpVerifyRequest;
import com.backend.hypershop.dto.schema.GlobalResponse;

public interface AuthService {

    GlobalResponse<?> sendConsumerLoginOtp(OtpRequest request);


    GlobalResponse<?> verifyConsumerLoginOtp(OtpVerifyRequest request);

    GlobalResponse<?> sendRiderLoginOtp(OtpRequest request);

    GlobalResponse<?> sendManagerLoginOtp(OtpRequest request);

    GlobalResponse<?> sendAdminLoginOtp(OtpRequest request);
}
