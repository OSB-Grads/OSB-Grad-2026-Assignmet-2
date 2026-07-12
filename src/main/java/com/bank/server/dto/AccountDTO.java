package com.bank.server.dto;

import java.math.BigDecimal;
import com.bank.server.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    private String id;
    private String accountNumber;
    private String customerId;
    private String productId;
    private BigDecimal balance;
    private AccountStatus status;
    private boolean isLocked;
}