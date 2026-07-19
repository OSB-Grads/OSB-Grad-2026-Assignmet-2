package com.bank.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UsernameAvailabilityResponse {
    private boolean available;
    private String message;
}
