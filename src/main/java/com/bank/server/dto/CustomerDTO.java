package com.bank.server.dto;

import com.bank.server.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {

    private String id;

    @NotBlank(message = "First name can't be blank")
    private String firstName;

    @NotBlank(message = "Last name can't be blank")
    private String lastName;

    @NotBlank(message = "Date of birth can't be blank")
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "Date of birth must be in YYYY-MM-DD format"
    )
    private String dateOfBirth;

    @NotBlank(message = "Email can't be blank")
    @Email(message = "Enter a valid email address")
    @Size(max = 100, message = "Email can't exceed 100 characters")
    private String email;

    @NotBlank(message = "Phone number can't be blank")
    @Pattern(
            regexp = "^\\d{10}$",
            message = "Phone number must contain exactly 10 digits"
    )
    private String phone;

    @NotBlank(message = "Address can't be blank")
    @Size(max = 255, message = "Address can't exceed 255 characters")
    private String address;

    @NotBlank(message = "National ID can't be blank")
    @Pattern(
            regexp = "^\\d{12}$",
            message = "National ID must contain exactly 12 digits"
    )
    private String nationalId;
}