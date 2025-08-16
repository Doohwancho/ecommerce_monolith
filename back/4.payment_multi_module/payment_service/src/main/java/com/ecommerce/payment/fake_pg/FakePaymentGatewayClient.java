package com.ecommerce.payment.fake_pg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.Duration;

@Slf4j
@Component
public class FakePaymentGatewayClient {
    
    /**
     * 가짜 PG사로 결제 요청을 보내는 메서드
     * @param orderId 주문 ID
     * @param amount 금액
     * @return Mono<PGResultCode> 결제 결과
     */
    public Mono<PGResultCode> requestPayment(String orderId, BigDecimal amount) {
        long delayMillis = (long) (Math.random() * 1800) + 200; // 0.2초 ~ 2.0초
    
        // 시나리오 5가지
        int scenario = orderId.length() % 5;
    
        switch (scenario) {
            case 0: // 성공
                return Mono.just(PGResultCode.SUCCESS)
                    .delayElement(Duration.ofMillis(delayMillis));
            case 1: // case 2.1) 명시적 실패 (잔액 부족)
                return Mono.just(PGResultCode.FAILURE_INSUFFICIENT_FUNDS)
                    .delayElement(Duration.ofMillis(delayMillis));
            case 2: // case 2.2) 재시도 가능한 일시적 실패
                log.warn("Simulating transient failure for order [{}]", orderId);
                return Mono.just(PGResultCode.FAILURE_TRANSIENT_BANK_ERROR)
                    .delayElement(Duration.ofMillis(delayMillis));
            case 3: // case 2.3) 서비스 레이어 타임아웃 유발
                return Mono.just(PGResultCode.SUCCESS) // 실제로는 성공했으나 응답이 늦는 경우
                    .delayElement(Duration.ofSeconds(3));
            default: // case 3.1) 명시적 실패 (중복 주문)
                return Mono.just(PGResultCode.FAILURE_DUPLICATE_ORDER)
                    .delayElement(Duration.ofMillis(delayMillis));
        }
    }
    
    // 거래 상태 조회 API 시뮬레이션
    public Mono<PGResultCode> inquirePaymentStatus(String paymentKey) {
        // paymentKey의 해시코드를 기반으로, 타임아웃이 발생했던 거래의 "실제 결과"를 시뮬레이션
        // 해시코드가 짝수이면 성공, 홀수이면 실패로 가정
        PGResultCode actualResult = (paymentKey.hashCode() % 2 == 0) ? PGResultCode.SUCCESS : PGResultCode.FAILURE_INSUFFICIENT_FUNDS;
        
        log.info("Inquiring payment status for [{}]. Actual result was: {}", paymentKey, actualResult);
        
        // 조회 API도 약간의 네트워크 지연이 있을 수 있음
        return Mono.just(actualResult).delayElement(Duration.ofMillis(300));
    }
    
    // 환불 요청
    public Mono<Boolean> requestRefund(String refundKey, BigDecimal amount) {
        log.info("Requesting refund to PG for key [{}] amount [{}].", refundKey, amount);
        // 환불은 대부분 성공한다고 가정
        return Mono.just(true).delayElement(Duration.ofSeconds(1));
    }
}
