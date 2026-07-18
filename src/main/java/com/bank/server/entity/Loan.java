package com.bank.server.entity;

import com.bank.server.enums.LoanCategory;
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
    @Column(name = "id")
    private String id;

    @NotNull(message = "Customer is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull(message = "Loan category is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "loan_category", nullable = false)
    private LoanCategory loanCategory;

    @NotNull(message = "Requested amount is required")
    @DecimalMin(value = "0.01")
    @Column(name = "requested_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal requestedAmount;

    @NotNull(message = "Offered rate is required")
    @Column(name = "offered_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal offeredRate;

    @NotNull(message = "Loan status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LoanStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

}