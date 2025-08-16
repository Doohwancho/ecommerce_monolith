package com.ecommerce.common.event;

import com.ecommerce.common.dto.PaymentEventDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentFailedEvent extends ApplicationEvent {
    // ✨ Payment 엔티티 대신 PaymentEventDto를 멤버로 가짐
    private final PaymentEventDto paymentEventDto;
    
    public PaymentFailedEvent(Object source, PaymentEventDto paymentEventDto) {
        super(source);
        this.paymentEventDto = paymentEventDto;
    }
}