package com.ecommerce.payment.scheduler;

import com.ecommerce.payment.entity.Refund;
import com.ecommerce.payment.entity.RefundStatus;
import com.ecommerce.payment.event.RefundCompletedEvent;
import com.ecommerce.payment.event.RefundFailedEvent;
import com.ecommerce.payment.fake_pg.FakePaymentGatewayClient;
import com.ecommerce.payment.repository.RefundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefundScheduler {
    
    private final RefundRepository refundRepository;
    private final FakePaymentGatewayClient pgClient;
    private final ApplicationEventPublisher eventPublisher;
    
    @Scheduled(fixedDelay = 60000) // 1분마다 실행
    public void processRefundRequests() {
        log.info("Starting refund processing job...");
        refundRepository.findByStatus(RefundStatus.REFUND_REQUESTED)
            .flatMap(this::executeRefund)
            .doOnComplete(() -> log.info("Finished refund processing job."))
            .subscribe();
    }
    
    private Mono<Refund> executeRefund(Refund refund) {
        return updateRefundStatus(refund, RefundStatus.REFUND_PROCESSING)
            .flatMap(processingRefund -> pgClient.requestRefund(processingRefund.getRefundKey(), processingRefund.getAmount())
                .flatMap(isSuccess -> {
                    RefundStatus finalStatus = isSuccess ? RefundStatus.REFUND_COMPLETED : RefundStatus.REFUND_FAILED;
                    return updateRefundStatus(processingRefund, finalStatus);
                })
                .doOnSuccess(finalRefund -> {
                    if (finalRefund.getStatus() == RefundStatus.REFUND_COMPLETED) {
                        eventPublisher.publishEvent(new RefundCompletedEvent(this, finalRefund));
                    } else if (finalRefund.getStatus() == RefundStatus.REFUND_FAILED) {
                        eventPublisher.publishEvent(new RefundFailedEvent(this, finalRefund));
                    }
                })
                .onErrorResume(error -> {
                    log.error("Refund failed for refundKey [{}].", refund.getRefundKey(), error);
                    return updateRefundStatus(refund, RefundStatus.REFUND_FAILED);
                })
            );
    }
    
    private Mono<Refund> updateRefundStatus(Refund refund, RefundStatus status) {
        Refund updatedRefund = refund.toBuilder()
            .status(status)
            .processedAt(LocalDateTime.now())
            .build();
        return refundRepository.save(updatedRefund);
    }
}
