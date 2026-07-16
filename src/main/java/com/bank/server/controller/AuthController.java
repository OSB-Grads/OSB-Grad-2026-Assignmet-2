package com.bank.server.controller;

import com.bank.server.dto.AuthDTO;
import com.bank.server.dto.CustomerDTO;
import com.bank.server.dto.request.LoginRequestDTO;
import com.bank.server.dto.request.RegisterRequestDTO;
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
    public AuthDTO register(@RequestBody RegisterRequestDTO request,
                            @RequestBody CustomerDTO dto) {

        return authService.register(request,dto);
    }

    @PostMapping("/login")
    public AuthDTO login(@RequestBody LoginRequestDTO request) {

        authService.login(request);
        return authService.login(request);
    }
}