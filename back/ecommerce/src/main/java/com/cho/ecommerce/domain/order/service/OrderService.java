package com.cho.ecommerce.domain.order.service;

import com.cho.ecommerce.api.domain.OrderDTO;
import com.cho.ecommerce.domain.order.domain.OrderItemDetails;
import com.cho.ecommerce.domain.order.entity.OrderEntity;
import com.cho.ecommerce.domain.order.entity.nativeQuery.OrderSalesStatisticsInterface;
import com.cho.ecommerce.domain.order.mapper.OrderMapper;
import com.cho.ecommerce.domain.order.mapper.TimeMapper;
import com.cho.ecommerce.domain.order.repository.OrderRepository;
import com.cho.ecommerce.global.error.exception.business.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private TimeMapper timeMapper;
    
    public OrderEntity createOrder(OrderEntity order) {
        return orderRepository.save(order);
    }
    
    public OrderEntity getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(
            () -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }
    
    public List<OrderItemDetails> findOrderItemDetailsByUsername(String username) {
        return orderRepository.getOrderItemDetailsByUsername(
            username).orElseThrow(() -> new ResourceNotFoundException(
            "No order details found for username: " + username));
    }
    
    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public OrderEntity updateOrder(Long orderId, OrderDTO orderDetails) {
        //1. check whether order exists
        OrderEntity orderEntity = getOrderById(orderId);
    
        //TODO - what to update?
        //2. update order status
        orderEntity.setOrderStatus(orderDetails.getOrderStatus());
        
        //3. save order
        return orderRepository.save(orderEntity);
    }
    
    public void deleteOrder(Long orderId) {
        OrderEntity order = getOrderById(orderId);
        orderRepository.delete(order);
    }
    
    public List<com.cho.ecommerce.api.domain.OrderSalesStatisticsResponseDTO> findMaxSalesProductAndAverageRatingAndTotalSalesPerCategoryDuringSixMonths() {
        List<OrderSalesStatisticsInterface> queryResults = orderRepository.findMaxSalesProductAndAverageRatingAndTotalSalesPerCategoryDuringSixMonths();
        List<com.cho.ecommerce.api.domain.OrderSalesStatisticsResponseDTO> list = new ArrayList<>();
    
        queryResults.forEach(result -> {
            com.cho.ecommerce.api.domain.OrderSalesStatisticsResponseDTO dto = new com.cho.ecommerce.api.domain.OrderSalesStatisticsResponseDTO();
            dto.setCategoryId(result.getCategoryId());
            dto.setCategoryName(result.getCategoryName());
            dto.setNumberOfProductsPerCategory(result.getNumberOfProductsPerCategory());
            dto.setAverageRating(result.getAverageRating());
            dto.setTotalSalesPerCategory(result.getTotalSalesPerCategory());
            dto.setProductId(result.getProductId());
            dto.setTopSalesProductName(result.getTopSalesProductName());
            dto.setTopSalesOfProduct(result.getTopSalesOfProduct());
            
            list.add(dto);
        });
        
        return list;
    }
}
