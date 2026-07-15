package com.bank.server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestDTO {
    private String loanCategoryId;
    private BigDecimal requestedAmount;
    private String disbursementAccountId;
}