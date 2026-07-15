package com.bank.server.entity;

import com.bank.server.enums.LoanStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "loan")
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    @NotBlank(message = "Loan id cannot be blank")
    @Column
    private String id;

    @NotNull(message = "Customer is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull(message = "Loan category is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_category_id", nullable = false)
    private LoanCategory loanCategory;

    @NotNull(message = "Requested amount is required")
    @DecimalMin(value = "0.01", message = "Requested amount must be greater than 0")
    @Column(name = "requested_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal requestedAmount;

    @NotNull(message = "Maximum eligible amount is required")
    @DecimalMin(value = "0.01", message = "Maximum eligible amount must be greater than 0")
    @Column(name = "max_eligible_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal maxEligibleAmount;

    @NotNull(message = "Offered rate is required")
    @DecimalMin(value = "0.01", message = "Offered rate must be greater than 0")
    @Column(name = "offered_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal offeredRate;

    @NotNull(message = "Loan status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LoanStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disbursement_account_id", nullable = false)
    private Account disbursementAccount;
}