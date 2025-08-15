package com.ecommerce.payment.repository;

import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentStatus;
import java.time.LocalDateTime;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PaymentRepository extends ReactiveCrudRepository<Payment, Long> {
    Mono<Payment> findByOrderId(String orderId);
    //추가: 특정 상태(ex. UNKNOWN)의 모든 결제 건을 조회
    Flux<Payment> findByPaymentStatusAndUpdatedAtBefore(PaymentStatus status, LocalDateTime updatedAt);
}
