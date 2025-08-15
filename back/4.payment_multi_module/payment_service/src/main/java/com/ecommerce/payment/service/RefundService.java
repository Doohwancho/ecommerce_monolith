package com.ecommerce.payment.service;

import com.ecommerce.payment.entity.PaymentStatus;
import com.ecommerce.payment.entity.Refund;
import com.ecommerce.payment.entity.RefundStatus;
import com.ecommerce.payment.repository.PaymentRepository;
import com.ecommerce.payment.repository.RefundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefundService {
    
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    
    public Mono<Refund> requestRefund(String orderId, BigDecimal amount, String reason) {
        return paymentRepository.findByOrderId(orderId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Original payment not found for order: " + orderId)))
            .flatMap(payment -> {
                if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
                    return Mono.error(new IllegalStateException("Cannot refund a non-completed payment."));
                }
                if (payment.getAmount().compareTo(amount) < 0) {
                    return Mono.error(new IllegalArgumentException("Refund amount cannot exceed the original payment amount."));
                }
                
                Refund refund = Refund.builder()
                    .paymentId(payment.getId())
                    .refundKey(UUID.randomUUID().toString())
                    .amount(amount)
                    .status(RefundStatus.REFUND_REQUESTED)
                    .reason(reason)
                    .createdAt(LocalDateTime.now())
                    .build();
                
                return refundRepository.save(refund);
            });
    }
}
