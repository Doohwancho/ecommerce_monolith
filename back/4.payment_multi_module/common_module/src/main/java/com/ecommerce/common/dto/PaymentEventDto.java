package com.ecommerce.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEventDto {
    
    private Long paymentId; // 결제 DB ID
    private String orderId; // 주문 ID
    private String paymentKey; // PG사 거래 키
    
    private BigDecimal amount; // 결제 금액
    
    private String paymentStatus; // 최종 결제 상태 (e.g., "FAILED", "CANCELLED")
    
    private String failureCode; // 실패 코드
    private String failureMessage; // 실패 메시지
    
    private LocalDateTime requestedAt; // 결제 요청 시각 (Payment.createdAt)
    private LocalDateTime processedAt; // 최종 처리 시각 (Payment.updatedAt)
}