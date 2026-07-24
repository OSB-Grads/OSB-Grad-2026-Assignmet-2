package com.bank.server.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // DEBUG
        System.out.println(
                "JWT FILTER: " +
                        request.getMethod() + " " +
                        request.getRequestURI()
        );

        String authHeader = request.getHeader("Authorization");

        // DEBUG
        System.out.println("AUTH HEADER = " + authHeader);

        // No JWT? Continue normally.
        // This is expected for /login and /register.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            System.out.println("NO BEARER TOKEN -> continuing filter chain");

            filterChain.doFilter(request, response);
            return;
        }

        try {

            String jwt = authHeader.substring(7);

            System.out.println("BEARER TOKEN FOUND");

            String customerId = jwtService.extractCustomerId(jwt);

            System.out.println("CUSTOMER ID FROM JWT = " + customerId);

            UserDetails userDetails =
                    customUserDetailsService.loadUserByCustomerId(customerId);

            System.out.println(
                    "USER DETAILS LOADED = " +
                            userDetails.getUsername()
            );

            if (jwtService.isTokenValid(jwt, userDetails)
                    && SecurityContextHolder
                    .getContext()
                    .getAuthentication() == null) {

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

                System.out.println("AUTHENTICATION SET SUCCESSFULLY");
            }

        } catch (JwtException | IllegalArgumentException e) {

            System.out.println(
                    "JWT ERROR = " + e.getMessage()
            );

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");

            response.getWriter().write("""
                    {
                      "code": "UNAUTHORIZED",
                      "message": "Invalid or expired JWT"
                    }
                    """);

            return;
        }

        filterChain.doFilter(request, response);
    }
}