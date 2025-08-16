package com.ecommerce.payment.scheduler;

import com.ecommerce.payment.entity.PaymentStatus;
import com.ecommerce.payment.fake_pg.FakePaymentGatewayClient;
import com.ecommerce.payment.repository.PaymentRepository;
import com.ecommerce.payment.service.PaymentService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class StalePaymentScheduler {
    
    private final PaymentRepository paymentRepository;
    private final FakePaymentGatewayClient pgClient;
    private final PaymentService paymentService;
    
    /**
     * 15분마다 실행되어, 30분 이상 'PROCESSING' 상태에 머물러 있는 결제 건을 조회하고 정리합니다.
     * 이는 시스템 장애 등으로 인해 결제 프로세스가 중간에 멈춰버린 '좀비' 데이터를 처리하기 위함입니다.
     */
    @Scheduled(fixedDelay = 900000) // 15분마다 실행 (15 * 60 * 1000 ms)
    public void reconcileStaleProcessingPayments() {
        // 30분 이상 처리 중인 결제 건을 찾기 위한 시간 기준 설정
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);
        
        log.info("Starting job to reconcile payments stuck in PROCESSING state before {}", thirtyMinutesAgo);
        
        // 'PROCESSING' 상태이고, 마지막 업데이트가 30분 이전인 결제 건들만 조회
        paymentRepository.findByPaymentStatusAndUpdatedAtBefore(PaymentStatus.PROCESSING, thirtyMinutesAgo)
            .flatMap(stalePayment -> {
                log.warn("Found stale payment in PROCESSING state: orderId [{}], updatedAt [{}]",
                    stalePayment.getOrderId(), stalePayment.getUpdatedAt());
                
                // PG사에 해당 결제의 최종 상태를 조회
                return pgClient.inquirePaymentStatus(stalePayment.getPaymentKey())
                    .flatMap(pgResult -> {
                        log.info("Inquiry result for stale payment order [{}]: {}", stalePayment.getOrderId(), pgResult);
                        // PaymentService의 기존 결과 처리 로직을 재사용하여 최종 상태로 업데이트
                        return paymentService.handlePGResponse(stalePayment, pgResult);
                    })
                    .onErrorResume(error -> {
                        // PG사 조회 중 에러 발생 시 로그만 남기고 다음 건으로 넘어감 (다음 스케줄링 시 재시도)
                        log.error("Error during inquiring status for stale payment order [{}]", stalePayment.getOrderId(), error);
                        return Mono.empty();
                    });
            })
            .doOnComplete(() -> log.info("Finished reconciling stale processing payments job."))
            .subscribe();
    }
}
