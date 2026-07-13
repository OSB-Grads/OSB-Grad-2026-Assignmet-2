package com.bank.server.dto;

import com.bank.server.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthDTO {
    private String id;
    private String username;
    private String passwordHash;
    private Role role;
}
