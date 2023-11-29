package com.cho.ecommerce.domain.order.service;

import com.cho.ecommerce.api.domain.OrderDTO;
import com.cho.ecommerce.api.domain.OrderItemDetailsDTO;
import com.cho.ecommerce.domain.order.domain.OrderItemDetails;
import com.cho.ecommerce.domain.order.entity.OrderEntity;
import com.cho.ecommerce.api.domain.OrderSalesStatisticsDTO;
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
    
    public OrderDTO createOrder(OrderDTO order) {
        OrderEntity orderEntity = orderMapper.orderDTOToOrderEntity(order);
        OrderEntity savedOrderEntity = orderRepository.save(orderEntity);
        return orderMapper.orderEntityToOrderDTO(savedOrderEntity);
    }
    
    public OrderEntity getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(
            () -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }
    
    public OrderDTO getOrderByIdForOrderDTO(Long orderId) {
        OrderEntity orderEntity = getOrderById(orderId);
        return orderMapper.orderEntityToOrderDTO(orderEntity);
    }
    
    public List<OrderItemDetailsDTO> findOrderItemDetailsByUsername(String username) {
        List<OrderItemDetails> orderItemDetails = orderRepository.getOrderItemDetailsByUsername(
            username).orElseThrow(() -> new ResourceNotFoundException(
            "No order details found for username: " + username));
        
        return orderMapper.orderItemDetailsListToDTOList(orderItemDetails);
    }
    
    public List<OrderDTO> getAllOrders() {
        List<OrderEntity> orderEntityList = orderRepository.findAll();
        return orderEntityList.stream().map(orderMapper::orderEntityToOrderDTO)
            .collect(Collectors.toList());
    }
    
    public OrderDTO updateOrder(Long orderId, OrderDTO orderDetails) {
        OrderEntity orderEntity = getOrderById(orderId);
        
        orderEntity.setOrderStatus(orderDetails.getOrderStatus());
        
        OrderDTO orderToSave = orderMapper.orderEntityToOrderDTO(orderEntity);
        
        //TODO - what to update?
        
        return createOrder(orderToSave);
    }
    
    public void deleteOrder(Long orderId) {
        OrderEntity order = getOrderById(orderId);
        orderRepository.delete(order);
    }
    
    public List<OrderSalesStatisticsDTO> findMaxSalesProductAndAverageRatingAndTotalSalesPerCategoryDuringSixMonths() {
        List<OrderSalesStatisticsInterface> queryResults = orderRepository.findMaxSalesProductAndAverageRatingAndTotalSalesPerCategoryDuringSixMonths();
        List<OrderSalesStatisticsDTO> list = new ArrayList<>();
    
        queryResults.forEach(result -> {
            OrderSalesStatisticsDTO dto = new OrderSalesStatisticsDTO();
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
