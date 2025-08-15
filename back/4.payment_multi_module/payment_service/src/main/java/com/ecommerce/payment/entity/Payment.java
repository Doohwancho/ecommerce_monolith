package com.ecommerce.payment.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("payments")
@Getter
@Builder(toBuilder = true) // toBuilder=true는 객체 복사를 통한 일부 필드 변경에 유용
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    private Long id;
    private String orderId;
    private BigDecimal amount;
    private PaymentStatus paymentStatus; // Enum 타입으로 변경
    private String paymentKey; // PG사로 요청을 보낼 때 사용하는 고유 키
    private int attemptCount; // 시도 횟수
    private String failureCode;
    private String failureMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt; // 상태 변경 시각
}
