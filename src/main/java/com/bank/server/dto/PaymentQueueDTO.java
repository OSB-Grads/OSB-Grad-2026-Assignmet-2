package com.bank.server.dto;

import com.bank.server.enums.PaymentStatus;
import com.bank.server.enums.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentQueueDTO {

    private String id;

    @NotNull(message = "Payment type is required")
    private PaymentType type;

    @NotBlank(message = "Target account ID cannot be empty")
    private String targetAccountId;

    @NotBlank(message = "Real world account ID cannot be empty")
    private String realWorldAccountId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    private PaymentStatus status;

    private String failureReason;

    private LocalDateTime createdAt;

    private LocalDateTime processedAt;
}
