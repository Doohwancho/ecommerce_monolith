package com.ecommerce.payment.entity;

public enum RefundStatus {
    REFUND_REQUESTED, // 환불 요청됨
    REFUND_PROCESSING,  // 환불 처리 중
    REFUND_COMPLETED,   // 환불 완료
    REFUND_FAILED       // 환불 실패
}