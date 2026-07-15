package com.bank.server.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequestDTO {
    private BigDecimal minOperatingBalance;
    private BigDecimal interestRate;
    private Long termMonths;
}
