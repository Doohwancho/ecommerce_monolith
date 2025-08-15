package com.ecommerce.payment.event;

import com.ecommerce.payment.entity.Refund;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RefundFailedEvent extends ApplicationEvent {
    private final Refund refund;
    public RefundFailedEvent(Object source, Refund refund) {
        super(source);
        this.refund = refund;
    }
}