package com.ecommerce.payment.event;

import com.ecommerce.payment.entity.Payment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentFailedEvent extends ApplicationEvent {
    private final Payment payment;
    
    public PaymentFailedEvent(Object source, Payment payment) {
        super(source);
        this.payment = payment;
    }
}