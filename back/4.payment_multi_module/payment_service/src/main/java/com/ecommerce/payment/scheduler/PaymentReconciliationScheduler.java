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

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentReconciliationScheduler {
    
    private final PaymentRepository paymentRepository;
    private final FakePaymentGatewayClient pgClient;
    private final PaymentService paymentService; // 상태 업데이트 로직 재사용
    
    // 5분마다 실행 (fixedDelay = 300000ms)
    @Scheduled(fixedDelay = 300000)
    public void reconcileUnknownPayments() {
        // 현재 시간으로부터 5분 전을 기준으로 설정
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
    
        log.info("Starting payment reconciliation job for UNKNOWN statuses updated before {}", fiveMinutesAgo);
    
        // 새로운 쿼리 메서드를 사용하여 UNKNOWN 상태가 된 지 5분 이상 지난 건들만 조회
        // Flux<Payment> 가져옴.
        paymentRepository.findByPaymentStatusAndUpdatedAtBefore(PaymentStatus.UNKNOWN, fiveMinutesAgo)
            .flatMap(payment -> {
                log.info("Reconciling payment for order [{}] (updated at: {})", payment.getOrderId(), payment.getUpdatedAt());
                // pg사에 결제 요청 재시도
                return pgClient.inquirePaymentStatus(payment.getPaymentKey())
                    // PG사로부터 받은 결과(pgResult)를 가지고 .handlePGResponse 호출
                    // pgResult가 SUCCESS이면, newStatus는 PaymentStatus.COMPLETED
                    // pgResult가 FAILURE...이면, newStatus는 PaymentStatus.FAILED
                    // ...로 상태를 업데이트하고, DB에 저장함
                    .flatMap(pgResult -> paymentService.handlePGResponse(payment, pgResult));
            })
            // 작업 끝나고 로그 찍는 콜백함수
            .doOnComplete(() -> log.info("Finished payment reconciliation job."))
            // .subscribe()를 호출한 단일 스레드가 리액티브 체인(스트림)을 따라 작업을 시작
            // I/O 작업을 만나면 대기하지 않음:
            // 데이터베이스에 쿼리를 보내거나 외부 API를 호출하는 등 시간이 걸리는 I/O 작업을 만나면,
            // 스레드는 결과를 기다리지 않습니다.
            // 대신 "작업이 끝나면 이 다음 일을 해줘"라고 콜백을 등록하고 즉시 다른 일을 처리하러 갑니다.
            // DB 응답이 오거나 API 결과가 도착하면(이벤트 발생), 이벤트 루프가 이를 감지하고 등록해 둔 콜백(예: .flatMap()의 뒷부분)을 워커 스레드(Worker Thread)에서 다시 실행합니다.
            .subscribe();
    }
}