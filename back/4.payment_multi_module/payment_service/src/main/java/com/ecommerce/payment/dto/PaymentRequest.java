package com.ecommerce.payment.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class PaymentRequest {
    private String orderId;
    private BigDecimal amount;
}