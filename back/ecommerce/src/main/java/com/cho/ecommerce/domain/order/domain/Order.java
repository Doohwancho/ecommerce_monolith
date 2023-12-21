package com.cho.ecommerce.domain.order.domain;

import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.order.entity.OrderItemEntity;
import com.cho.ecommerce.api.domain.OrderRequestDTO;
import com.cho.ecommerce.global.error.exception.business.OrderItemsRequestedByMoreThanOneUser;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
    
    public static boolean checkAllOrderItemsFromSameUser(
        List<OrderRequestDTO> orderRequests) {
        Set<Long> memberIds = orderRequests.stream()
            .map(OrderRequestDTO::getMemberId)
            .collect(Collectors.toSet());
        
        if(memberIds.size() != 1) {
            throw new OrderItemsRequestedByMoreThanOneUser("주문이 한명 이상의 유저로부터 요청이 왔습니다.");
        }
        return memberIds.size() == 1;
    }
}