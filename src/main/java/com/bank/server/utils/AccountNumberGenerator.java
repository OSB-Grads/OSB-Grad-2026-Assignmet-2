package com.bank.server.utils;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;

@Component
public class AccountNumberGenerator {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final static SecureRandom random = new SecureRandom();

    public static String generate() {
        StringBuilder sb = new StringBuilder("OSBA");

        for (int i = 0; i < 6; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }

        sb.append("2026");

        return sb.toString();
    }
}