package com.bank.server.dto;

import com.bank.server.enums.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {
    private String id;
    private String customerId;
    private String loanCategoryId;
    private BigDecimal requestedAmount;
    private BigDecimal maxEligibleAmount;
    private BigDecimal offeredRate;
    private LoanStatus status;
}