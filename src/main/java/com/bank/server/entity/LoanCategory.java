package com.bank.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "loan_category")
@NoArgsConstructor
@AllArgsConstructor
public class LoanCategory {

    @Id
    @NotBlank(message = "Id cannot be blank")
    @Column(name = "id")
    private String id;

    @NotBlank(message = "Name cannot be blank")
    @Column(nullable = false, unique = true)
    private String name;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Interest rate must be greater than 0")
    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @NotNull(message = "Eligibility multiplier is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Eligibility multiplier must be greater than 0")
    @Column(name = "eligibility_multiplier", nullable = false, precision = 5, scale = 2)
    private BigDecimal eligibilityMultiplier;


}
