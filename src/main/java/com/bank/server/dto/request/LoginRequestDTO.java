package com.bank.server.dto.request;

import lombok.Data;

@Data
public class LoginRequestDTO {

    private String username;
    private String password;

}