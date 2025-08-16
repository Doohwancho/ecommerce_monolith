package com.ecommerce.payment.service;

import com.ecommerce.payment.dto.PaymentRequest;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentStatus;
import com.ecommerce.common.dto.PaymentEventDto;
import com.ecommerce.common.event.PaymentCancelledEvent;
import com.ecommerce.common.event.PaymentFailedEvent;
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
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import reactor.util.retry.Retry;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final TransactionalOperator transactionalOperator;
    private final PaymentRepository paymentRepository;
    private final FakePaymentGatewayClient pgClient;
    private final ApplicationEventPublisher eventPublisher;
    
    public Mono<Payment> processPayment(PaymentRequest paymentRequest) {
        // case 3.1) 중복 결제 요청 (Idempotency)
        // processPayment 메서드 초반에 orderId를 조회하여 중복 처리를 방지, 멱등성 체크
        return paymentRepository.findByOrderId(paymentRequest.getOrderId())
            //Mono.defer()을 썼기 때문에, db에 payment가 비어있을 때만 처리 실행한다. (왜냐면 Mono.defer()는 .subscribe() 되지 않은 이상 실행되지 않기 떄문)
            .switchIfEmpty(Mono.defer(() -> processNewPayment(paymentRequest))) //Mono.just()는 호출시점에 데이터가 즉시 생성된다면, Mono.defer()는 요청자가 데이터를 subscribe(요청)할 때까지 미루는 것. (backpressure의 일종)
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
    
        // 1단계: 초기 'PROCESSING' 상태 저장 (첫 번째 트랜잭션)
        // save 작업만 트랜잭션으로 묶어 즉시 커밋합니다.
        Mono<Payment> initialSave = paymentRepository.save(newPayment)
            .as(transactionalOperator::transactional);
    
        return initialSave
            .flatMap(processingPayment ->
                // 2단계: PG 요청 (트랜잭션 밖에서 실행)
                pgClient.requestPayment(processingPayment.getOrderId(), processingPayment.getAmount())
                    // PG 응답 처리 로직
                    .flatMap(pgResult ->
                        // 3단계: 결과 처리 (두 번째 트랜잭션)
                        handlePGResponse(processingPayment, pgResult)
                            .as(transactionalOperator::transactional)
                    )
                    // 요구사항 2.3 - 응답 시간 초과) 타임아웃 시간을 2초로 설정
                    .timeout(Duration.ofSeconds(2))
                    // 요구사항 2.2 - 일시적 실패) 일시적 장애 대응을 위한 재시도 로직, 500ms 간격으로 2번 재시도 한다.
                    // TransientPaymentException 타입의 오류에 대해서만 재시도하도록 필터링 강화
                    .retryWhen(Retry.backoff(2, Duration.ofMillis(500))
                        .filter(throwable -> throwable instanceof TransientPaymentException) // 타임아웃은 재시도하지 않음
                        .doBeforeRetry(retrySignal -> log.info("Retrying payment for order [{}], attempt: {}", processingPayment.getOrderId(), retrySignal.totalRetries() + 1))
                    )
                    // 실패시 후처리
                    .onErrorResume(throwable -> {
                        // 요구사항 2.3 - 응답시간 초과)
                        // 응답시간 초과는 결제가 PG사에서 처리 되었는지 안되었는지 확인 불가능 상태라, 상태를 UNKNOWN으로 바꾸고,
                        // 스케쥴러로 5분마다 PG사에 상태 확인 후, 후처리 한다.
                        if (throwable instanceof TimeoutException) {
                            log.error("Payment for order [{}] timed out. Status set to UNKNOWN for manual reconciliation.", processingPayment.getOrderId(), throwable);
                            // 상태를 unknown상태로 마크하고, scheduler가 5분마다 재시도 처리하게 한다.
                            // 3단계 (실패): 실패 상태 업데이트 (별도의 새 트랜잭션)
                            return updatePaymentStatus(processingPayment, PaymentStatus.UNKNOWN).as(transactionalOperator::transactional);
                        }
                        // TransientPaymentException 재시도 실패 시 FAILED 처리
                        log.error("Payment failed for order [{}] after all retries.", processingPayment.getOrderId(), throwable);
                        // 3단계 (실패): 실패 상태 업데이트 (별도의 새 트랜잭션)
                        return updatePaymentStatus(processingPayment, PaymentStatus.FAILED).as(transactionalOperator::transactional);
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
                    eventPublisher.publishEvent(new PaymentFailedEvent(this, toEventDto(savedPayment)));
                }
                if (savedPayment.getPaymentStatus() == PaymentStatus.FAILED) {
                    eventPublisher.publishEvent(new PaymentFailedEvent(this, toEventDto(savedPayment)));
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
                // case1) 이미 완료된 결제는 취소가 아닌 '환불' 프로세스를 타야 함
                if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
                    return Mono.error(new IllegalStateException("Cannot cancel a completed payment. Please process a refund."));
                }
                // 진행중이거나 실패한 결제는 취소 가능
                if (payment.getPaymentStatus() == PaymentStatus.PENDING || payment.getPaymentStatus() == PaymentStatus.PROCESSING) {
                    // case2) 아직 PG 요청 전
                    // 사용자가 결제 버튼을 누르고 PG사 화면으로 넘어가기 직전에 취소한 경우. 이때 CANCELLED로 상태를 바꾸는 것은 완벽히 안전합니다.
                    
                    // case3) PG 요청 후 응답 대기 중
                    // 이 상태에서 취소 요청이 들어와도, 우리는 이미 나간 PG 요청을 되돌릴 수 없습니다. CANCELLED로 상태를 바꾸더라도, 나중에 PG로부터 '성공' 응답이 올 수 있습니다 (데이터 불일치 발생 가능).
                    // 따라서 일단 PG 요청이 나간 후에는 '취소'가 아니라, 결과를 기다린 후 그 결과에 따라 행동해야 합니다.
                    // 만약 PG 응답이 '성공'으로 오면, 사용자가 중간에 취소를 원했더라도 이미 늦었으므로, 별도의 '환불(Refund)' 트랜잭션을 시작해야 합니다.
                    // 만약 PG 응답이 '실패' 또는 '타임아웃'으로 오면, 그때 CANCELLED 또는 FAILED로 상태를 변경하면 됩니다.
                    
                    // 상태 업데이트 후 이벤트 발행 로직
                    return updatePaymentStatus(payment, PaymentStatus.CANCELLED)
                        .doOnSuccess(cancelledPayment ->
                            eventPublisher.publishEvent(new PaymentCancelledEvent(this, toEventDto(cancelledPayment)))
                        );
                }
                // 이미 취소/실패된 건은 그대로 반환
                return Mono.just(payment);
            });
    }
    
    /**
     * ✨ 추가: Payment 엔티티를 PaymentEventDto로 변환하는 private 헬퍼 메서드
     */
    private PaymentEventDto toEventDto(Payment payment) {
        return PaymentEventDto.builder()
            .paymentId(payment.getId())
            .orderId(payment.getOrderId())
            .paymentKey(payment.getPaymentKey())
            .amount(payment.getAmount())
            .paymentStatus(payment.getPaymentStatus().name())
            .failureCode(payment.getFailureCode())
            .failureMessage(payment.getFailureMessage())
            .requestedAt(payment.getCreatedAt())
            .processedAt(payment.getUpdatedAt())
            .build();
    }
}

