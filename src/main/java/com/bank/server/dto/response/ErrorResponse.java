package com.bank.server.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
public class ErrorResponse {

    private String code;
    private String message;
}
