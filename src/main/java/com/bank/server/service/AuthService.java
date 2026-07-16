package com.bank.server.service;

import com.bank.server.dto.LoginRequest;
import com.bank.server.dto.LoginResponse;
import com.bank.server.dto.RegisterRequest;
import com.bank.server.dto.AuthDTO;
import com.bank.server.dto.CustomerDTO;
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
import com.bank.server.security.CustomUserDetails;
import com.bank.server.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final AuthMapper authMapper;
    private final CustomerService customerService;
    private final LoggerService loggerService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService  jwtService;
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public AuthDTO register(RegisterRequestDTO request, CustomerDTO customerDTO) {
        if (authRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException(
                    "AUTH_REGISTER",
                    "Username already exists"
            );
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
      customerDTO.setId(auth.getId());
        customerService.createCustomer(customerDTO);
        return authMapper.toDto(savedAuth);
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public AuthDTO login(LoginRequestDTO request) throws UserNotFoundException {

        Auth auth = authRepository.findByUsername(request.getUsername()).orElseThrow(() -> new UserNotFoundException(
                "AUTH_LOGIN",
                "User not found"
        ));
        if (!passwordEncoder.matches(request.getPassword(), auth.getPasswordHash())) {
            throw new InvalidCredentialsException(
                    "AUTH_LOGIN",
                    "Invalid username or password"
            );

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