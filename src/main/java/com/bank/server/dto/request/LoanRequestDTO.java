package com.bank.server.dto.request;

import com.bank.server.enums.LoanCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestDTO {

    @NotNull(message = "Loan category is required")
    private LoanCategory loanCategory;

    @NotNull(message = "Requested amount is required")
    @DecimalMin(value = "0.01")
    private BigDecimal requestedAmount;
}