package com.bank.server.dto.response;

import com.bank.server.enums.PaymentStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private String id;
    private PaymentStatus status;
    private String message;
}
