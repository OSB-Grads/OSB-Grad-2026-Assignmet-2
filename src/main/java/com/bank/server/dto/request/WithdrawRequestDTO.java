package com.bank.server.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawRequestDTO {

    @NotBlank(message = "Target account id is required")
    private String targetAccountId;

    @NotNull(message = "Amount is required")
    @Positive
    private BigDecimal amount;
}
