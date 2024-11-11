package com.cho.ecommerce.domain.member.domain;

import java.time.LocalDateTime;
import lombok.Value;
import org.apache.commons.validator.routines.EmailValidator;

@Value
public class Email {
    
    private final String value;
    
    LocalDateTime expiryTime;
    
    public Email(String value, LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
        if (!EmailValidator.getInstance().isValid(value)) {
            throw new IllegalArgumentException("Invalid email address");
        }
        this.value = value;
    }
}
