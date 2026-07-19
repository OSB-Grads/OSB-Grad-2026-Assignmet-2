package com.bank.server.controller;

import com.bank.server.dto.AuthDTO;
import com.bank.server.dto.CustomerDTO;
import com.bank.server.dto.LoginResponse;
import com.bank.server.dto.request.LoginRequestDTO;
import com.bank.server.dto.request.RegisterRequestDTO;
import com.bank.server.dto.response.UsernameAvailabilityResponse;
import com.bank.server.entity.Auth;
import com.bank.server.exception.UserNotFoundException;
import com.bank.server.security.JwtService;
import com.bank.server.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthDTO register(@RequestBody RegisterRequestDTO request,
                            @RequestBody CustomerDTO dto) {
        return authService.register(request,dto);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequestDTO request)
            throws UserNotFoundException {
        return authService.login(request);
    }
    @GetMapping("/check-username")
    public ResponseEntity<UsernameAvailabilityResponse> checkUsername(
            @RequestParam String username
    ) {
        UsernameAvailabilityResponse response =
                authService.checkUsernameAvailability(username);
        return ResponseEntity.ok(response);
    }
}