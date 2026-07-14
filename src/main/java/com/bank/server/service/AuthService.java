package com.bank.server.service;

import com.bank.server.dto.LoginRequest;
import com.bank.server.dto.LoginResponse;
import com.bank.server.dto.RegisterRequest;
import com.bank.server.entity.Auth;
import com.bank.server.enums.Role;
import com.bank.server.exception.InvalidCredentialsException;
import com.bank.server.exception.UserNotFoundException;
import com.bank.server.exception.UsernameAlreadyExistsException;
import com.bank.server.repository.AuthRepository;
import com.bank.server.security.CustomUserDetails;
import com.bank.server.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService  jwtService;

    public Auth register(RegisterRequest request) {

        if (authRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        Auth auth = Auth.builder()
                .id(UUID.randomUUID().toString())
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .build();

        return authRepository.save(auth);
    }

    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        Auth auth = authRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        CustomUserDetails userDetails = new CustomUserDetails(auth);
        String token = jwtService.generateToken(userDetails);
        return LoginResponse.builder()
                .token(token)
                .build();
    }
}