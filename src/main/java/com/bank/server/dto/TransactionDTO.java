package com.bank.server.dto;

import com.bank.server.enums.TransactionStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {

    private String id;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    private String fromAccountId;

    private String toAccountId;

    @NotBlank(message = "Transaction type is required")
    private String transactionType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Status is required")
    private TransactionStatus status;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}