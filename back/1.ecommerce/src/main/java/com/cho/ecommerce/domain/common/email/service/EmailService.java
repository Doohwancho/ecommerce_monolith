package com.cho.ecommerce.domain.common.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender emailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public void sendVerificationCode(String toEmail, String code, int expiryMinutes) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Your Verification Code");
        message.setText(String.format(
            "Your verification code is: %s\n" +
                "This code will expire in %d minutes.\n" +
                "If you didn't request this code, please ignore this email.",
            code, expiryMinutes));
        
        emailSender.send(message);
    }
    
    public void sendAccountLockedNotification(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Your Account has been Locked");
        message.setText(
            "Abnormal Activity Detected.\n" +
                "Your Account has been locked.\n" +
                "To Reactivate account, click 'forgot password?' on login page.\n" +
                "If you didn't request this code, please ignore this email.");
        
        emailSender.send(message);
    }
}