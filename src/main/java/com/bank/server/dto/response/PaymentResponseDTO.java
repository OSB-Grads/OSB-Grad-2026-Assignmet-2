package com.bank.server.dto.response;

import com.bank.server.enums.TransactionStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private String id;
    private TransactionStatus status;
    private String message;
}
