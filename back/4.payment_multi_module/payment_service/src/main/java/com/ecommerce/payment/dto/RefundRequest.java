package com.ecommerce.payment.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RefundRequest {
    private BigDecimal amount; // 환불 요청 금액
    private String reason;     // 환불 사유
}