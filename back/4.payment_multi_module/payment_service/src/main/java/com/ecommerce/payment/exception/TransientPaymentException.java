package com.ecommerce.payment.exception;

/**
 * 재시도 가능한 일시적인 결제 오류를 나타내는 예외 클래스입니다.
 */
public class TransientPaymentException extends RuntimeException {
    public TransientPaymentException(String message) {
        super(message);
    }
}