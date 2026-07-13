package com.bank.server.controller;

import com.bank.server.dto.LoginRequest;
import com.bank.server.dto.RegisterRequest;
import com.bank.server.entity.Auth;
import com.bank.server.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public Auth register(@RequestBody RegisterRequest request) {

        return authService.register(request);
    }

    @PostMapping("/login")
    public Auth login(@RequestBody LoginRequest request) {

        return authService.login(request);
    }

}
