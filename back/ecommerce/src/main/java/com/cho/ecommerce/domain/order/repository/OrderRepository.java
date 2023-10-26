package com.cho.ecommerce.domain.order.repository;

import com.cho.ecommerce.domain.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

}
