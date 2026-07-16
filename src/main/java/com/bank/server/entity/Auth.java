package com.bank.server.entity;

import com.bank.server.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "auth")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Auth {

    @Id
    @Column(name = "id")
    private String id;

    @NotBlank(message = "Username is required")
    @Column(name = "username", nullable = false, unique = true)
    private String username;


    @NotBlank(message = "Password is required")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;


    @NotBlank(message = "Role is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;
}