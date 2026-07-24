package com.bank.server.utils;

import com.bank.server.exception.UnauthorizedException;
import com.bank.server.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationUtil {

    public static Authentication getAuthentication() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException(
                    "AUTH_ERROR",
                    "User is not authenticated"
            );
        }

        return authentication;
    }

    public static CustomUserDetails getCurrentUser() {
        Object principal = getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails user) {
            return user;
        }

        throw new UnauthorizedException(
                "AUTH_ERROR",
                "User is not authenticated"
        );
    }

    public static String getCurrentCustomerId() {
        return getCurrentUser().getCustomerId();
    }
    public static String getCurrentCustomerIdOrNull() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();


        if (!(principal instanceof CustomUserDetails customUser)) {
            return null;
        }
        return customUser.getCustomerId();
    }
}
