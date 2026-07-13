package com.bank.server.dto;

import java.math.BigDecimal;
import com.bank.server.enums.AccountStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    private String id;
    private String accountNumber;
    @NotBlank(message = "Customer Id cannot be Blank")
    private String customerId;
    @NotBlank(message = "Product Id cannot be Blank")
    private String productId;
    private BigDecimal balance;
    private AccountStatus status;
    private boolean isLocked;
}