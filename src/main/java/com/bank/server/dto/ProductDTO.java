package com.bank.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private String id;
    private String productName;
    private String category;
    private BigDecimal interestRate;
    private BigDecimal minOperatingBalance;
    private Long termMonths;
}
