package com.bank.server.dto;

import com.bank.server.enums.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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

    @NotBlank(message = "Category is required")
    private ProductCategory category;

    @NotNull(message = "Interest Rate should not be blank")
    @Positive(message = "Interest Rate must be greater than 0")
    private BigDecimal interestRate;

    @NotNull(message = "Minimum operating balance is required")
    @PositiveOrZero(message = "Minimum Operating Balance cannot be negative")
    private BigDecimal minOperatingBalance;

    @Positive(message = "Terms months should be positive")
    private Long termMonths;
}
