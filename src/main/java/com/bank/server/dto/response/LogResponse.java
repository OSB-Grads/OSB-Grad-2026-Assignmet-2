package com.bank.server.dto.response;

import com.bank.server.enums.LogType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogResponse {

    private String id;

    @NotBlank
    private String customerId;

    @NotBlank
    private String action;

    private String details;

    @NotBlank
    private LogType status;

    private LocalDateTime createdAt;
}