package com.bank.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private String id;
    @NotBlank
    private String username;
    @NotBlank
    private String role;          // CUSTOMER or ADMIN
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String dateOfBirth;   // ISO date string, e.g. "1990-05-21"
    @NotBlank
    private String email;
    @NotBlank
    private String phone;
    @NotBlank
    private String address;
    @NotBlank
    private String nationalId;



}
