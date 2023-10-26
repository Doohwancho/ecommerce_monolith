package com.cho.ecommerce.domain.order.service;

import com.cho.ecommerce.domain.order.domain.Order;
import com.cho.ecommerce.domain.order.entity.OrderEntity;
import com.cho.ecommerce.domain.order.mapper.OrderMapper;
import com.cho.ecommerce.domain.order.repository.OrderRepository;
import com.cho.ecommerce.global.error.ResourceNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderMapper orderMapper;
    
    public OrderEntity createOrder(OrderEntity order) {
        return orderRepository.save(order);
    }
    
    public OrderEntity getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }
    
    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public OrderEntity updateOrder(Long orderId, OrderEntity orderDetails) {
        OrderEntity orderEntity = getOrderById(orderId);
        Order order = orderMapper.orderEntityToOrder(orderEntity);
        
        order.setOrderDate(orderDetails.getOrderDate());
        order.setOrderStatus(orderDetails.getOrderStatus());
        
        OrderEntity orderEntityToBeSaved = orderMapper.orderToOrderEntity(order);
        
        return orderRepository.save(orderEntityToBeSaved);
    }
    
    public void deleteOrder(Long orderId) {
        OrderEntity order = getOrderById(orderId);
        orderRepository.delete(order);
    }
}
