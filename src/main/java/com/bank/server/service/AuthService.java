package com.bank.server.service;

import com.bank.server.dto.AuthDTO;
import com.bank.server.dto.request.LoginRequestDTO;
import com.bank.server.dto.request.RegisterRequestDTO;
import com.bank.server.entity.Auth;
import com.bank.server.enums.LogType;
import com.bank.server.enums.Role;
import com.bank.server.exception.InvalidCredentialsException;
import com.bank.server.exception.UserNotFoundException;
import com.bank.server.exception.UsernameAlreadyExistsException;
import com.bank.server.mapper.AuthMapper;
import com.bank.server.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
`

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final AuthMapper authMapper;
    private final LoggerService loggerService;
    private final PasswordEncoder passwordEncoder;

    public AuthDTO register(RegisterRequestDTO request) {

        if (authRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        Auth auth = Auth.builder()
                .id(UUID.randomUUID().toString())
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .build();

        Auth savedAuth = authRepository.save(auth);

        loggerService.log(
                "AUTH_REGISTER",
                "User registered successfully with username: " + savedAuth.getUsername(),
                LogType.SUCCESS
        );

        return authMapper.toDto(savedAuth);
    }

    public AuthDTO login(LoginRequestDTO request) throws UserNotFoundException {

        Auth auth = authRepository.findByUsername(request.getUsername()).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!passwordEncoder.matches(request.getPassword(), auth.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
        loggerService.log(
                "AUTH_REGISTER",
                "User logged in successfully with username "
                        + auth.getUsername(),
                LogType.SUCCESS
        );
        return authMapper.toDto(auth);
    }
}