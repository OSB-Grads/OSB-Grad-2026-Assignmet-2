package com.bank.server.dto.request;

import com.bank.server.enums.LoanCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestDTO {

    private LoanCategory loanCategory;
    private BigDecimal requestedAmount;
    private String disbursementAccountId;
}