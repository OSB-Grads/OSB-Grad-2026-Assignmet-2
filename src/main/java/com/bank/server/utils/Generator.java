package com.bank.server.utils;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;
import java.util.UUID;

@Component
public class Generator {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final static SecureRandom random = new SecureRandom();

    public static String generateAccountNumber() {
        StringBuilder sb = new StringBuilder("OSBA");

        for (int i = 0; i < 6; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }

        sb.append("2026");

        return sb.toString();
    }

    public static String generateProductName() {
        SecureRandom secureRandom = new SecureRandom();

        StringBuilder sb = new StringBuilder("OSBP");

        for (int i = 0; i < 6; i++) {
            sb.append(CHARS.charAt(secureRandom.nextInt(CHARS.length())));
        }

        sb.append("2026");

        return sb.toString();
    }

    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }
}
