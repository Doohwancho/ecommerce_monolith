package com.cho.ecommerce.domain.order.repository;

import com.cho.ecommerce.domain.order.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

}
