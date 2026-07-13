package com.bank.server.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductCategory {
    SAVINGS("Savings"),
    FIXED_DEPOSITS("Fixed Deposits"),
    LIMITED_ACCESS("Limited Access");

    private final String displayName;
}
