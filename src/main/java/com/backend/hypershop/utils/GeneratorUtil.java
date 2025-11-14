// GeneratorUtil.java
package com.backend.hypershop.utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GeneratorUtil {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * Generate ID with prefix
     * Format: PREFIX_YYYYMMDDHHMMSS_RANDOM6
     * Example: USER_20251110174530_A5B2C3
     *
     * Use case: Users, Products, Orders, Stores
     */
    public static String generateId(String prefix) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = generateRandomString(6);
        return prefix + "_" + timestamp + "_" + random;
    }

    /**
     * Generate ID without prefix (Simple format)
     * Format: YYYYMMDDHHMMSS_RANDOM8
     * Example: 20251110174530_AB12CD34
     *
     * Use case: Temporary IDs, Session IDs, Tokens
     */
    public static String generateId() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = generateRandomString(8);
        return timestamp + "_" + random;
    }

    /**
     * Generate random alphanumeric string
     * Helper method for ID generation
     */
    private static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }


    public static String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
