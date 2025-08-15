package com.ecommerce.payment.repository;

import com.ecommerce.payment.entity.Refund;
import com.ecommerce.payment.entity.RefundStatus;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface RefundRepository extends ReactiveCrudRepository<Refund, Long> {
    Flux<Refund> findByStatus(RefundStatus status);
}