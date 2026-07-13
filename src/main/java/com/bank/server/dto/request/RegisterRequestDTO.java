package com.bank.server.dto.request;

import lombok.Data;

@Data
public class RegisterRequestDTO {

    private String username;
    private String password;
}