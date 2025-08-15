package com.ecommerce.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("refunds")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Refund {
    @Id
    private Long id;
    private Long paymentId; // 원본 결제 ID (Foreign Key)
    private String refundKey; // 환불 요청용 고유 키
    private BigDecimal amount;
    private RefundStatus status;
    private String reason; // 환불 사유
    private LocalDateTime createdAt;
    private LocalDateTime processedAt; // 처리 완료 시각
}