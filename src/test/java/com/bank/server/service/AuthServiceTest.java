package com.bank.server.service;

import com.bank.server.dto.AuthDTO;
import com.bank.server.dto.CustomerDTO;
import com.bank.server.dto.LoginResponse;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "Password@123";
    private static final String ENCODED_PASSWORD = "encoded-password";
    private static final String JWT_TOKEN = "generated-jwt-token";

    @Mock
    private AuthRepository authRepository;

    @Mock
    private AuthMapper authMapper;

    @Mock
    private CustomerService customerService;

    @Mock
    private LoggerService loggerService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetails userDetails;

    @InjectMocks
    private AuthService authService;

    private RegisterRequestDTO registerRequest;
    private LoginRequestDTO loginRequest;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequestDTO();
        registerRequest.setUsername(USERNAME);
        registerRequest.setPassword(PASSWORD);

        loginRequest = new LoginRequestDTO();
        loginRequest.setUsername(USERNAME);
        loginRequest.setPassword(PASSWORD);

        customerDTO = new CustomerDTO();
    }

    @Test
    void register_shouldCreateAuthAndCustomer_whenUsernameIsAvailable() {
        when(authRepository.existsByUsername(USERNAME)).thenReturn(false);

        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);

        when(authRepository.save(any(Auth.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(authMapper.toDto(any(Auth.class))).thenAnswer(invocation -> {
                    Auth auth = invocation.getArgument(0);

                    AuthDTO dto = new AuthDTO();
                    dto.setId(auth.getId());
                    dto.setUsername(auth.getUsername());
                    dto.setRole(auth.getRole());

                    return dto;
                });

        AuthDTO response = authService.register(registerRequest, customerDTO);

        ArgumentCaptor<Auth> captor = ArgumentCaptor.forClass(Auth.class);

        verify(authRepository).save(captor.capture());

        Auth savedAuth = captor.getValue();

        assertNotNull(response);
        assertDoesNotThrow(() -> UUID.fromString(savedAuth.getId()));

        assertEquals(USERNAME, savedAuth.getUsername());
        assertEquals(ENCODED_PASSWORD, savedAuth.getPasswordHash());
        assertEquals(Role.CUSTOMER, savedAuth.getRole());
        assertEquals(savedAuth.getId(), customerDTO.getId());
        assertEquals(savedAuth.getId(), response.getId());

        verify(customerService).createCustomer(customerDTO);

        verify(loggerService).log(
                "AUTH_REGISTER",
                "User registered successfully with username: " + USERNAME,
                LogType.SUCCESS);
    }

    @Test
    void register_shouldThrowException_whenUsernameAlreadyExists() {
        when(authRepository.existsByUsername(USERNAME)).thenReturn(true);

        UsernameAlreadyExistsException exception = assertThrows(UsernameAlreadyExistsException.class,
                        () -> authService.register(registerRequest, customerDTO));

        assertEquals("Username already exists", exception.getMessage());

        verify(authRepository, never()).save(any());
        verifyNoInteractions(
                passwordEncoder,
                customerService,
                loggerService,
                authMapper);
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() throws UserNotFoundException {
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(userDetails.getUsername()).thenReturn(USERNAME);

        when(jwtService.generateToken(userDetails)).thenReturn(JWT_TOKEN);

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(JWT_TOKEN, response.getAccessToken());

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

        verify(authenticationManager).authenticate(captor.capture());

        assertEquals(USERNAME, captor.getValue().getPrincipal());

        assertEquals(PASSWORD, captor.getValue().getCredentials());

        verify(loggerService).log(
                "AUTH_LOGIN",
                "User logged in successfully with username: " + USERNAME,
                LogType.SUCCESS);
    }

    @Test
    void login_shouldNotGenerateToken_whenCredentialsAreInvalid() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));

        verifyNoInteractions(jwtService, loggerService);
    }
}