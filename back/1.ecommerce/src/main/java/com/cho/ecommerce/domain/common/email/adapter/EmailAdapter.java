package com.cho.ecommerce.domain.common.email.adapter;

import com.cho.ecommerce.domain.common.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailAdapter {
    
    private final EmailService emailService;
    
    public void sendVerificationCode(String email, String code, int expiryMinutes) {
        emailService.sendVerificationCode(email, code, expiryMinutes);
    }
    
    public void sendAccountLockedNotification(String email) {
        emailService.sendAccountLockedNotification(email);
    }
}
