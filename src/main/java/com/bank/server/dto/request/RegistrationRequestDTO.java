package com.bank.server.dto.request;

import com.bank.server.dto.CustomerDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequestDTO {
    @Valid
    @NotNull(message = "Authentication details are required")
    private RegisterRequestDTO auth;

    @Valid
    @NotNull(message = "Customer details are required")
    private CustomerDTO customer;
}