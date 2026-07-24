package com.bank.server.service;

import com.bank.server.dto.AuthDTO;
import com.bank.server.dto.CustomerDTO;
import com.bank.server.dto.LoginResponse;
import com.bank.server.dto.request.LoginRequestDTO;
import com.bank.server.dto.request.RegisterRequestDTO;
import com.bank.server.dto.response.UsernameAvailabilityResponse;
import com.bank.server.entity.Auth;
import com.bank.server.enums.LogType;
import com.bank.server.enums.Role;
import com.bank.server.exception.UsernameAlreadyExistsException;
import com.bank.server.mapper.AuthMapper;
import com.bank.server.repository.AuthRepository;
import com.bank.server.security.CustomUserDetails;
import com.bank.server.security.CustomUserDetailsService;
import com.bank.server.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Transactional
    public AuthDTO register(
            RegisterRequestDTO request,
            CustomerDTO customerDTO) {

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
                "User registered successfully with username: "
                        + savedAuth.getUsername(),
                LogType.SUCCESS
        );

        customerDTO.setId(auth.getId());
        customerService.createCustomer(customerDTO);

        return authMapper.toDto(savedAuth);
    }
    public LoginResponse login(LoginRequestDTO request) {
        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getUsername(),
                                    request.getPassword()
                            )
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);

        loggerService.log(
                "AUTH_LOGIN",
                "User logged in successfully with username: "
                        + userDetails.getUsername(),
                LogType.SUCCESS
        );

        return LoginResponse.builder()
                .token(token)
                .build();
        } catch (Exception e) {
            e.printStackTrace(); 
            throw e;
        }
    }
    public UsernameAvailabilityResponse checkUsernameAvailability(String username) {
        boolean available = !authRepository.existsByUsername(username);
        return new UsernameAvailabilityResponse(
                available,
                available
                        ? "Username is available"
                        : "Username is already taken"
        );
    }
}