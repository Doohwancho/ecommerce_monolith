package com.ecommerce.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentResponse {
    private Long paymentId;
    private String orderId;
    private String paymentKey;
    private String status;
    private String message;
}