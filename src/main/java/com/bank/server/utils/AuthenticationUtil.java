package com.bank.server.utils;

import com.bank.server.exception.UnauthorizedException;
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
}