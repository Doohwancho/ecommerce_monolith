package com.ecommerce.payment.event;

import com.ecommerce.common.event.PaymentCancelledEvent;
import com.ecommerce.common.event.PaymentFailedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentEventListener {
    
//    @EventListener
//    public void handlePaymentFailed(PaymentFailedEvent event) {
//        // 후속 처리 로직
//        log.warn("Payment failed! Notifying other services for orderId: {}", event.getPaymentEventDto().getOrderId());
//
//        // TODO 1: 주문 서비스(Order Service)에 결제 실패 알림 (e.g., Kafka/RabbitMQ 메시지 발행)
//        //       -> 주문 상태를 '결제 실패'로 변경하고, 재고를 원복하도록 요청
//
//        // TODO 2: 알림 서비스(Notification Service)에 사용자 알림 요청
//        //       -> 사용자에게 "결제에 실패했습니다. 사유: [실패 메시지]" 와 같은 SMS/이메일 발송
//        String failureMessage = event.getPaymentEventDto().getFailureMessage();
//        log.info("Notifying user about failure: {}", failureMessage);
//    }
//
//    // 결제 취소 이벤트 핸들러
//    @EventListener
//    public void handlePaymentCancelled(PaymentCancelledEvent event) {
//        log.warn("Payment cancelled! Notifying other services for orderId: {}", event.getPaymentEventDto().getOrderId());
//
//        // TODO: 주문 서비스(Order Service)에 결제 취소 알림
//        //       -> 주문 상태를 '주문 취소'로 변경하고, 상품 재고를 원상 복구하도록 요청
//    }
}