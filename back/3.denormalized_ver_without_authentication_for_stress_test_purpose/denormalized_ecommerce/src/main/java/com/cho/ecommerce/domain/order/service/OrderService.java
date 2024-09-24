package com.cho.ecommerce.domain.order.service;


import com.cho.ecommerce.domain.order.dto.OrderItemsResponseDTO;
import com.cho.ecommerce.domain.order.entity.DenormalizedOrderEntity;
import com.cho.ecommerce.domain.order.mapper.OrderMapper;
import com.cho.ecommerce.domain.order.repository.OrderRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    
    @Autowired
    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }
    
    public List<OrderItemsResponseDTO> getOrderItemsByUsername(String username) {
        List<DenormalizedOrderEntity> orders = orderRepository.findOrdersByMemberName(username);
        
        return orders.stream()
            .map(orderMapper::convertToOrderItemsResponseDTO)
            .collect(Collectors.toList());
    }
}