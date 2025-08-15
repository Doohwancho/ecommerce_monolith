package com.ecommerce.payment.fake_pg;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PGResultCode {
    SUCCESS("0000", "Success"),
    FAILURE_INSUFFICIENT_FUNDS("E001", "Insufficient funds"),
    FAILURE_DUPLICATE_ORDER("E002", "Duplicate order ID"),
    FAILURE_TIMEOUT("E003", "Timeout"),
    FAILURE_TRANSIENT_BANK_ERROR("E999", "Transient bank error, please retry");
    
    private final String code;
    private final String message;
}