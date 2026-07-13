package com.bank.server.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer{
    @Id
    private String id;
    @Column(nullable = false,unique = true)
    @NotBlank(message = "Username can't be blank")
    private String username;
    private String role;
    @Column(nullable = false)
    @NotBlank(message = "FirstName cant be blank")
    private String firstName;
    @Column(nullable = false)
    @NotBlank(message = "LastName can't be blank")
    private String lastName;
    @Column(nullable = false)
    private String dateOfBirth;
    @Column(length = 100)
    @Email(message = "Enter a valid email address")
    @Size(max = 100, message = "Email can't exceed more then 100 letters")
    private String email;
    @Column (length = 20)
    @NotBlank(message = "Enter a valid phone number")
    @Size(min = 10, max = 10, message = "Phone number should be 10 digit")
    private String phone;
    @NotBlank(message = "Address can't be empty")
    private String address;
    @Column(length = 50)
    @NotBlank(message = "NationalId can't be empty")
    @Size(min = 12, max = 12, message = "National Id should be 12 digit")
    private String nationalId;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}



