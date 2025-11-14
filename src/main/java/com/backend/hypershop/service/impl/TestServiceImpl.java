package com.backend.hypershop.service.impl;

import com.backend.hypershop.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TestServiceImpl implements TestService {

    @Override
    @PreAuthorize("hasRole('CONSUMER')")
    public String hello() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("User ID: {}", auth.getName());
        return "hello";
    }
}