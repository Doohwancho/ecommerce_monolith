package com.ecommerce.monolith.domain.order.event;

import com.ecommerce.monolith.domain.order.repository.OrderRepository;
import com.ecommerce.monolith.domain.order.service.OrderService;
import com.ecommerce.monolith.domain.product.service.ProductService;
import com.ecommerce.common.event.PaymentCancelledEvent;
import com.ecommerce.common.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {
    
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final OrderService orderService;
    
    /**
     * 결제 실패 이벤트를 수신하여 보상 트랜잭션을 처리합니다.
     * (주문 상태 변경 + 상품 재고 원복)
     */
    @EventListener
    public void handlePaymentFailure(PaymentFailedEvent event) {
        String orderIdStr = event.getPaymentEventDto().getOrderId();
        log.info("Received PaymentFailedEvent for orderId: {}", orderIdStr);
    
        // ✨ 복잡한 로직 없이, OrderService에 작업 위임
        try {
            orderService.processPaymentFailure(Long.parseLong(orderIdStr));
        } catch (Exception e) {
            log.error("Failed to execute compensating transaction for orderId: {}", orderIdStr, e);
            // TODO: 보상 트랜잭션 자체에 실패했을 경우, 심각한 오류이므로 반드시 알림 처리
        }
    }
    /**
     * 결제 취소 이벤트를 수신하여 보상 트랜잭션을 처리합니다.
     * (주문 상태 변경 + 상품 재고 원복)
     */
    @EventListener
    public void handlePaymentCancellation(PaymentCancelledEvent event) {
        String orderIdStr = event.getPaymentEventDto().getOrderId();
        log.info("Received PaymentCancelledEvent for orderId: {}", orderIdStr);
    
        try {
            orderService.processPaymentCancellation(Long.parseLong(orderIdStr));
        } catch (Exception e) {
            log.error("Failed to execute compensating transaction for cancelled payment. OrderId: {}", orderIdStr, e);
            // TODO: 보상 트랜잭션 실패 시 알림 처리
        }
    }
}