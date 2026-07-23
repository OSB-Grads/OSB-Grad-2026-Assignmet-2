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
        System.out.println("-------------------" + authentication + "----------------");
        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUserDetails)) {

            throw new UnauthorizedException(
                    "AUTH_ERROR",
                    "User is not authenticated"
            );
        }
        return authentication;
    }
    public static CustomUserDetails getCurrentUser() {
        Object principal = getAuthentication().getPrincipal();
        return (CustomUserDetails) principal;

    }
    public static String getCurrentCustomerId() {
        System.out.println(getCurrentUser().getCustomerId());
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