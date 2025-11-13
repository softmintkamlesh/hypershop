package com.backend.hypershop.service;


import com.backend.hypershop.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TestService {

    @PreAuthorize("hasRole('CONSUMER')")
    public String hello() {
        // Utility class use karo
        String userId = SecurityUtil.getCurrentUserId();
        log.info("Current User ID: {}", userId);

        boolean isAuthenticated = SecurityUtil.isAuthenticated();
        log.info("Is Authenticated: {}", isAuthenticated);

        boolean isAdmin = SecurityUtil.hasRole("ADMIN");
        log.info("Is Admin: {}", isAdmin);

        boolean isConsumer = SecurityUtil.hasRole("CONSUMER");
        log.info("Is Consumer: {}", isConsumer);

        return "hello";
    }
}
