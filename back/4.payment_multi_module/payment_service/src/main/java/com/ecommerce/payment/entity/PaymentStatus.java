package com.ecommerce.payment.entity;

public enum PaymentStatus {
    PENDING,      // 대기
    PROCESSING,   // 처리 중
    COMPLETED,    // 성공
    FAILED,       // 실패
    CANCELLED,    // 취소
    UNKNOWN       // 타임아웃 등으로 상태 확인이 필요한 경우
}