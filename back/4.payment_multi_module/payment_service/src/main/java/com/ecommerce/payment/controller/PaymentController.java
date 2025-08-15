package com.ecommerce.payment.controller;

import com.ecommerce.payment.dto.PaymentRequest;
import com.ecommerce.payment.dto.PaymentResponse;
import com.ecommerce.payment.dto.RefundRequest;
import com.ecommerce.payment.repository.PaymentRepository;
import com.ecommerce.payment.service.PaymentService;
import com.ecommerce.payment.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;
    private final RefundService refundService;
    
    // 주문 요청
    @PostMapping("/process")
    public Mono<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        return paymentService.processPayment(request)
            .map(payment -> new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getPaymentKey(),
                payment.getPaymentStatus().name(), // Enum 이름을 문자열로 변환
                "Payment processing finished with status: " + payment.getPaymentStatus().name()
            ));
    }
    
    // 주문 취소
    @PostMapping("/{orderId}/cancel")
    public Mono<ResponseEntity<PaymentResponse>> cancelPayment(@PathVariable String orderId) {
        return paymentService.cancelPayment(orderId)
            .map(payment -> ResponseEntity.ok(new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getPaymentKey(),
                payment.getPaymentStatus().name(),
                "Payment cancellation processed."
            )))
            .onErrorResume(IllegalArgumentException.class, e -> // 존재하지 않는 주문
                Mono.just(ResponseEntity.notFound().build())
            )
            .onErrorResume(IllegalStateException.class, e -> // 이미 완료된 주문
                Mono.just(ResponseEntity.badRequest().body(new PaymentResponse(null, orderId, null, null, e.getMessage())))
            );
    }
    
    // 환불 신청
    @PostMapping("/{orderId}/refund")
    public Mono<ResponseEntity<String>> refundPayment(@PathVariable String orderId, @RequestBody RefundRequest refundRequest) {
        return refundService.requestRefund(orderId, refundRequest.getAmount(), refundRequest.getReason())
            .map(refund -> ResponseEntity.accepted().body("Refund request for order " + orderId + " has been accepted."))
            .onErrorResume(IllegalArgumentException.class, e -> Mono.just(ResponseEntity.status(404).body(e.getMessage())))
            .onErrorResume(IllegalStateException.class, e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }
}