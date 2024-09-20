package com.cho.ecommerce.domain.order.adapter;

import com.cho.ecommerce.api.domain.OrderDTO;
import com.cho.ecommerce.domain.order.domain.OrderItemDetails;
import com.cho.ecommerce.domain.order.entity.OrderEntity;
import com.cho.ecommerce.domain.order.mapper.OrderMapper;
import com.cho.ecommerce.domain.order.service.OrderService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderAdapter {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderMapper orderMapper;
    
    public List<com.cho.ecommerce.api.domain.OrderDTO> getAllOrders() {
        List<OrderEntity> orderEntityList = orderService.getAllOrders();
        return orderEntityList.stream().map(orderMapper::orderEntityToOrderDTO)
            .collect(Collectors.toList());
    }
    
    public com.cho.ecommerce.api.domain.OrderDTO getOrderByIdForOrderDTO(Long orderId) {
        OrderEntity orderEntity = orderService.getOrderById(orderId);
        return orderMapper.orderEntityToOrderDTO(orderEntity);
    }
    
    public com.cho.ecommerce.api.domain.OrderDTO updateOrder(Long orderId, OrderDTO order) {
        OrderEntity orderEntity = orderService.updateOrder(orderId, order);
        return orderMapper.orderEntityToOrderDTO(orderEntity);
    }
    
    public List<com.cho.ecommerce.api.domain.OrderItemDetailsResponseDTO> findOrderItemDetailsByUsername(
        String username) {
        List<OrderItemDetails> orderItemDetailsByUsername = orderService.findOrderItemDetailsByUsername(
            username);
        return orderMapper.orderItemDetailsListToDTOList(orderItemDetailsByUsername);
    }
}
