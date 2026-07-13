package com.bank.server.service;

import com.bank.server.dto.LoginRequest;
import com.bank.server.dto.RegisterRequest;
import com.bank.server.entity.Auth;
import com.bank.server.enums.Role;
import com.bank.server.exception.InvalidCredentialsException;
import com.bank.server.exception.UserNotFoundException;
import com.bank.server.exception.UsernameAlreadyExistsException;
import com.bank.server.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;

    public Auth register(RegisterRequest request) {

        if (authRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        Auth auth = Auth.builder()
                .id(UUID.randomUUID().toString())
                .username(request.getUsername())
                .passwordHash(request.getPassword())
                .role(Role.CUSTOMER)
                .build();

        return authRepository.save(auth);
    }

    public Auth login(LoginRequest request) {

        Auth auth = authRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!auth.getPasswordHash().matches(request.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        return auth;
    }
}