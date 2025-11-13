package com.backend.hypershop.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Optional;

public class SecurityUtil {

    /**
     * Get current authenticated user's userId
     */
    public static String getCurrentUserId() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getName)
                .orElse(null);
    }

    /**
     * Get current authentication object
     */
    public static Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Get current user's authorities/roles
     */
    public static Collection<? extends GrantedAuthority> getCurrentAuthorities() {
        return Optional.ofNullable(getCurrentAuthentication())
                .map(Authentication::getAuthorities)
                .orElse(null);
    }

    /**
     * Check if user is authenticated
     */
    public static boolean isAuthenticated() {
        Authentication auth = getCurrentAuthentication();
        return auth != null && auth.isAuthenticated();
    }

    /**
     * Check if user has specific role
     */
    public static boolean hasRole(String role) {
        Authentication auth = getCurrentAuthentication();
        if (auth == null) return false;
        
        return auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }

    /**
     * Get principal object
     */
    public static Object getPrincipal() {
        return Optional.ofNullable(getCurrentAuthentication())
                .map(Authentication::getPrincipal)
                .orElse(null);
    }
}
