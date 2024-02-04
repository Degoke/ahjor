package com.degoke.ahjor.util;

import java.util.Random;

public class Generator {
    public static char[] generateOtp(int len) {
        String numbers = "0123456789";

        Random random_method = new Random();

        char[] otp = new char[len];

        for (int i = 0; i < len; i++) {
            otp[i] = numbers.charAt(random_method.nextInt(numbers.length()));
        }

        return otp;
    }
}
