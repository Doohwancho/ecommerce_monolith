package com.cho.ecommerce.domain.order.domain;

import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.order.entity.OrderItemEntity;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order {
    
    private Long orderId;
    
    private LocalDateTime orderDate;
    
    private String orderStatus;
    
    private UserEntity member;
    
    private Set<OrderItemEntity> orderItems;
    
    public void setOrderDate(LocalDateTime dateTime) {
        this.orderDate = dateTime;
    }
    
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}