package com.cho.ecommerce.domain.order.repository;

import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.order.domain.OrderItemDetails;
import java.util.List;
import java.util.Optional;

public interface OrderRepositoryCustom {
    List<OrderItemDetails> getOrderItemDetailsByUsername(String username);

}
