package com.bank.server.dto.response;

import com.bank.server.enums.InboxStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InboxResponseDTO {
    private String id;
    private InboxStatus status;
    private String message;
}
