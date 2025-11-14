package com.backend.hypershop.utils;

public class AppUtil {

    public static String maskMobile(String mobile) {
        if (mobile.length() >= 10) {
            return mobile.substring(0, 2) + "******" + mobile.substring(8);
        }
        return "******";
    }
}
