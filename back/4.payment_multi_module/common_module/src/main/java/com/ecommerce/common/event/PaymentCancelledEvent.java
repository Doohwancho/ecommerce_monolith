package com.ecommerce.common.event;

import com.ecommerce.common.dto.PaymentEventDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentCancelledEvent extends ApplicationEvent {
    // ✨ Payment 엔티티 대신 DTO를 사용
    private final PaymentEventDto paymentEventDto;
    
    public PaymentCancelledEvent(Object source, PaymentEventDto paymentEventDto) {
        super(source);
        this.paymentEventDto = paymentEventDto;
    }
}
