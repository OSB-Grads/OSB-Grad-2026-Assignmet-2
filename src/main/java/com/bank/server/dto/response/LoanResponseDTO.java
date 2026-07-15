package com.bank.server.dto.response;

import com.bank.server.enums.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponseDTO {
    private String id;
    private String loanCategory;
    private BigDecimal requestedAmount;
    private BigDecimal maxEligibleAmount;
    private BigDecimal offeredRate;
    private LoanStatus status;
}