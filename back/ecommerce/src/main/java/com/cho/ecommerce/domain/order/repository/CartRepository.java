package com.cho.ecommerce.domain.order.repository;

import com.cho.ecommerce.domain.order.entity.CartEntity;
import com.cho.ecommerce.domain.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {

}
