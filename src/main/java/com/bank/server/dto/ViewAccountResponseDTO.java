package com.bank.server.dto;

import java.math.BigDecimal;
import com.bank.server.enums.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewAccountResponseDTO {

    private String id;
    private String accountNumber;
    private BigDecimal balance;
    private String productName;
    private ProductCategory category;
}