package com.bank.server.dto;

import com.bank.server.enums.InboxStatus;
import com.bank.server.enums.InboxMessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InboxDTO {

    private String id;

    @NotBlank(message = "Correlation Id is required")
    private String correlationId;

    private String transactionId;

    @NotNull(message = "Message type is required")
    private InboxMessageType messageType;

    @NotEmpty(message = "Payload is required")
    private Map<String,Object> payload;

    private InboxStatus status;

    private String reason;

    private LocalDateTime createdAt;

    private LocalDateTime processedAt;
}
