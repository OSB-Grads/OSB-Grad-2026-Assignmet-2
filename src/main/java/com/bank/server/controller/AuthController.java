package com.bank.server.controller;

import com.bank.server.dto.AuthDTO;
import com.bank.server.dto.CustomerDTO;
import com.bank.server.dto.LoginResponse;
import com.bank.server.dto.request.LoginRequestDTO;
import com.bank.server.dto.request.RegisterRequestDTO;
import com.bank.server.security.CustomUserDetails;
import com.bank.server.service.CustomerService;
import lombok.RequiredArgsConstructor;
import com.bank.server.entity.Auth;
import com.bank.server.exception.UserNotFoundException;
import com.bank.server.security.JwtService;
import com.bank.server.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.bank.server.dto.request.RegistrationRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
//    private final JwtService jwtService;
    private final CustomerService customerService;

//    @PostMapping("/register")
//    public AuthDTO register(@RequestBody RegisterRequestDTO request,
//                            @RequestBody CustomerDTO dto) {
//        return authService.register(request,dto);
//    }

//    @PostMapping("/login")
//    public LoginResponse login(@RequestBody LoginRequestDTO request)
//            throws UserNotFoundException {
//        return authService.login(request);
//    }
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegistrationRequestDTO registrationRequest) {
        AuthDTO registeredUser = authService.register(
                registrationRequest.getAuth(),
                registrationRequest.getCustomer()
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User Created Successfully");
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/me")
    public ResponseEntity<CustomerDTO> getCurrentProfile(Authentication authentication) {
        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();
        String customerId = currentUser.getCustomerId();
        CustomerDTO customer = customerService.getMyProfile(customerId);
        return ResponseEntity.ok(customer);
    }
}