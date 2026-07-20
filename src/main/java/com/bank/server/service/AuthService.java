package com.bank.server.service;
import com.bank.server.dto.LoginResponse;
import com.bank.server.dto.AuthDTO;
import com.bank.server.dto.CustomerDTO;
import com.bank.server.dto.request.LoginRequestDTO;
import com.bank.server.dto.request.RegisterRequestDTO;
import com.bank.server.entity.Auth;
import com.bank.server.enums.LogType;
import com.bank.server.enums.Role;
import com.bank.server.exception.UserNotFoundException;
import com.bank.server.exception.UsernameAlreadyExistsException;
import com.bank.server.mapper.AuthMapper;
import com.bank.server.repository.AuthRepository;
import com.bank.server.security.CustomUserDetails;
import com.bank.server.security.CustomUserDetailsService;
import com.bank.server.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private final CustomUserDetailsService customUserDetailsService;
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @Transactional
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
    public LoginResponse login(LoginRequestDTO request) throws UserNotFoundException {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );
        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();
        assert userDetails != null;
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
    }
}