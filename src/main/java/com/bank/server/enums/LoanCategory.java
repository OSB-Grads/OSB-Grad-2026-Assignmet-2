package com.bank.server.enums;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum LoanCategory {

    PERSONAL(
            "Personal Loan",
            new BigDecimal("12.50"),
            new BigDecimal("2.00")
    ),

    HOME(
            "Home Loan",
            new BigDecimal("8.50"),
            new BigDecimal("5.00")
    ),

    EDUCATION(
            "Education Loan",
            new BigDecimal("7.25"),
            new BigDecimal("3.00")
    );

    private final String displayName;
    private final BigDecimal interestRate;
    private final BigDecimal deltaPercentage;

    LoanCategory(String displayName,
                 BigDecimal interestRate,
                 BigDecimal deltaPercentage) {
        this.displayName = displayName;
        this.interestRate = interestRate;
        this.deltaPercentage = deltaPercentage;
    }

}