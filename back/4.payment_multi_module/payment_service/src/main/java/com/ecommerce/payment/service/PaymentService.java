package com.ecommerce.payment.service;

import com.ecommerce.payment.dto.PaymentRequest;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentStatus;
import com.ecommerce.payment.event.PaymentCancelledEvent;
import com.ecommerce.payment.event.PaymentFailedEvent;
import com.ecommerce.payment.exception.TransientPaymentException;
import com.ecommerce.payment.fake_pg.FakePaymentGatewayClient;
import com.ecommerce.payment.fake_pg.PGResultCode;
import com.ecommerce.payment.repository.PaymentRepository;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import reactor.util.retry.Retry;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final FakePaymentGatewayClient pgClient;
    private final ApplicationEventPublisher eventPublisher;
    
    public Mono<Payment> processPayment(PaymentRequest paymentRequest) {
        // case 3.1) 중복 결제 요청 (Idempotency): processPayment 메서드 초반에 orderId를 조회하여 중복 처리를 방지, 멱등성 체크
        return paymentRepository.findByOrderId(paymentRequest.getOrderId())
            .switchIfEmpty(Mono.defer(() -> processNewPayment(paymentRequest)))
            .flatMap(existingPayment -> {
                if (existingPayment.getPaymentStatus() == PaymentStatus.COMPLETED) {
                    log.warn("Order [{}] already has a completed payment.", paymentRequest.getOrderId());
                    return Mono.just(existingPayment); // 이미 성공한 결제라면 그대로 반환
                }
                // 실패, 취소, 확인필요 상태의 결제건이 있다면 새로 결제를 생성하여 진행
                if (existingPayment.getPaymentStatus() == PaymentStatus.FAILED ||
                    existingPayment.getPaymentStatus() == PaymentStatus.CANCELLED ||
                    existingPayment.getPaymentStatus() == PaymentStatus.UNKNOWN) {
                    return processNewPayment(paymentRequest);
                }
                // 처리중(PENDING, PROCESSING)인 결제건은 그대로 반환하여 중복 요청 방지
                log.info("Payment for order [{}] is already in progress.", paymentRequest.getOrderId());
                return Mono.just(existingPayment);
            });
    }
    
    private Mono<Payment> processNewPayment(PaymentRequest paymentRequest) {
        Payment newPayment = Payment.builder()
            .orderId(paymentRequest.getOrderId())
            .amount(paymentRequest.getAmount())
            .paymentStatus(PaymentStatus.PROCESSING) // PROCESSING 상태로 시작
            .paymentKey(UUID.randomUUID().toString())
            .attemptCount(1) // 시도 횟수는 1부터 시작
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    
        return paymentRepository.save(newPayment) // DB 저장 1회
            .flatMap(processingPayment ->
                pgClient.requestPayment(processingPayment.getOrderId(), processingPayment.getAmount())
                    // PG 응답 처리 로직
                    .flatMap(pgResult -> handlePGResponse(processingPayment, pgResult))
                    // 요구사항 2.3) 타임아웃 시간을 2초로 설정
                    .timeout(Duration.ofSeconds(2))
                    // 요구사항 2.2) 일시적 장애 대응을 위한 재시도 로직
                    // TransientPaymentException 타입의 오류에 대해서만 재시도하도록 필터링 강화
                    .retryWhen(Retry.backoff(2, Duration.ofMillis(500))
                        .filter(throwable -> throwable instanceof TransientPaymentException) // 타임아웃은 재시도하지 않음
                        .doBeforeRetry(retrySignal -> log.info("Retrying payment for order [{}], attempt: {}", processingPayment.getOrderId(), retrySignal.totalRetries() + 1))
                    )
                    // 요구사항 2.1 & 2.3) 명시적 실패 & 타임아웃 또는 최종 재시도 실패 시 처리 로직 업데이트
                    .onErrorResume(throwable -> {
                        if (throwable instanceof TimeoutException) {
                            log.error("Payment for order [{}] timed out. Status set to UNKNOWN for manual reconciliation.", processingPayment.getOrderId(), throwable);
                            // 상태를 unknown상태로 마크하고, scheduler가 5분마다 재시도 처리하게 한다.
                            return updatePaymentStatus(processingPayment, PaymentStatus.UNKNOWN);
                        }
                        // TransientPaymentException 재시도 모두 실패 시 FAILED 처리
                        log.error("Payment failed for order [{}] after all retries.", processingPayment.getOrderId(), throwable);
                        return updatePaymentStatus(processingPayment, PaymentStatus.FAILED);
                    })
            );
    }
    
    // 요구사항 2.1) PG 응답 처리 로직
    public Mono<Payment> handlePGResponse(Payment payment, PGResultCode pgResult) {
        PaymentStatus newStatus;
        switch (pgResult) {
            case SUCCESS:
                newStatus = PaymentStatus.COMPLETED;
                break;
            case FAILURE_TRANSIENT_BANK_ERROR:
                return Mono.error(new TransientPaymentException("Transient bank error for order: " + payment.getOrderId()));
            // 일시적 실패 시, 재시도를 유발하기 위해 커스텀 예외 발생
            case FAILURE_INSUFFICIENT_FUNDS:
            case FAILURE_DUPLICATE_ORDER:
            case FAILURE_TIMEOUT: // PG가 명시적으로 타임아웃을 응답한 경우
                newStatus = PaymentStatus.FAILED;
                log.info("PG response for order [{}]: {}, updating status to {}", payment.getOrderId(), pgResult, newStatus);
                return updatePaymentStatus(payment, newStatus, pgResult);
            default:
                log.error("Unexpected PGResultCode: {} for order [{}]", pgResult, payment.getOrderId());
                return Mono.error(new IllegalStateException("Unexpected PGResultCode: " + pgResult));
        }
        log.info("PG response for order [{}]: {}, updating status to {}", payment.getOrderId(), pgResult, newStatus);
        return updatePaymentStatus(payment, newStatus, null);
    }
    
    private Mono<Payment> updatePaymentStatus(Payment payment, PaymentStatus status) {
        return updatePaymentStatus(payment, status, null);
    }
    private Mono<Payment> updatePaymentStatus(Payment payment, PaymentStatus status, PGResultCode pgResult) {
        Payment.PaymentBuilder builder = payment.toBuilder()
            .paymentStatus(status)
            .attemptCount(payment.getAttemptCount() + 1)
            .updatedAt(LocalDateTime.now());
        
        // 실패했을 경우, 코드와 메시지를 함께 기록
        if (status == PaymentStatus.FAILED && pgResult != null) {
            builder.failureCode(pgResult.getCode())
                .failureMessage(pgResult.getMessage());
        }
    
        Payment updatedPayment = builder.build();
    
        return paymentRepository.save(updatedPayment)
            .doOnSuccess(savedPayment -> {
                // DB 저장 성공 후, 상태가 FAILED이면 이벤트 발행
                if (savedPayment.getPaymentStatus() == PaymentStatus.FAILED) {
                    eventPublisher.publishEvent(new PaymentFailedEvent(this, savedPayment));
                }
                // (확장) 성공 시 PaymentCompletedEvent를 발행할 수도 있음
            });
    }
    
    // 요구사항 3.3) 결제 취소 로직
    public Mono<Payment> cancelPayment(String orderId) {
        log.info("Attempting to cancel payment for order [{}]", orderId);
        return paymentRepository.findByOrderId(orderId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Payment not found for order: " + orderId)))
            .flatMap(payment -> {
                // 이미 완료된 결제는 취소가 아닌 '환불' 프로세스를 타야 함
                if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
                    return Mono.error(new IllegalStateException("Cannot cancel a completed payment. Please process a refund."));
                }
                // 진행중이거나 실패한 결제는 취소 가능
                if (payment.getPaymentStatus() == PaymentStatus.PENDING || payment.getPaymentStatus() == PaymentStatus.PROCESSING) {
                    // 상태 업데이트 후 이벤트 발행 로직
                    return updatePaymentStatus(payment, PaymentStatus.CANCELLED)
                        .doOnSuccess(cancelledPayment ->
                            eventPublisher.publishEvent(new PaymentCancelledEvent(this, cancelledPayment))
                        );
                }
                // 이미 취소/실패된 건은 그대로 반환
                return Mono.just(payment);
            });
    }
}

