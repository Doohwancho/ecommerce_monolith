package com.cho.ecommerce.domain.order.mapper;

import com.cho.ecommerce.domain.order.domain.Order.OrderItem;
import com.cho.ecommerce.domain.order.dto.CreateOrderRequestDTO;
import com.cho.ecommerce.domain.order.dto.OrderItemsResponseDTO;
import com.cho.ecommerce.domain.order.entity.DenormalizedOrderEntity;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderMapper {
    
    public OrderItemsResponseDTO convertToOrderItemsResponseDTO(DenormalizedOrderEntity entity) {
        OrderItemsResponseDTO dto = new OrderItemsResponseDTO();
        dto.setOrderId(entity.getOrderId());
        dto.setOrderDate(entity.getOrderDate());
        dto.setOrderStatus(entity.getOrderStatus());
        dto.setMemberName(entity.getMemberName());
        dto.setTotalPrice(entity.getTotalPrice());
        dto.setTotalQuantity(entity.getTotalQuantity());
        dto.setStreet(entity.getStreet());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setCountry(entity.getCountry());
        dto.setZipCode(entity.getZipCode());
        
        try {
            dto.setOrderItems(entity.getOrderItemsAsList());
        } catch (Exception e) {
            log.error("Error parsing order items: {}", entity.getOrderItems(), e);
            dto.setOrderItems(new ArrayList<>()); // Set empty list as fallback
        }
        
        return dto;
    }
    
    public DenormalizedOrderEntity convertToEntity(CreateOrderRequestDTO dto) {
        DenormalizedOrderEntity entity = new DenormalizedOrderEntity();
        entity.setMemberId(dto.getMemberId());
        entity.setMemberName(dto.getMemberName());
        entity.setMemberEmail(dto.getMemberEmail());
        entity.setStreet(dto.getStreet());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setCountry(dto.getCountry());
        entity.setZipCode(dto.getZipCode());
        
        // Set default values
        entity.setOrderDate(OffsetDateTime.now());
        entity.setOrderStatus("PENDING");
        
        // Calculate total price and quantity
        double totalPrice = 0;
        int totalQuantity = 0;
    
        List<OrderItem> orderItems = dto.getOrderItems();
        for (OrderItem item : orderItems) {
            totalPrice += item.getDiscountedPrice() * item.getQuantity(); //TODO - 원래대로라면 DB에 해당 item에 discount가 맞는지 validation 해줘야 함
            totalQuantity += item.getQuantity();
        }
        entity.setTotalPrice(totalPrice);
        entity.setTotalQuantity(totalQuantity);
        
        // Set order items
        try {
            entity.setOrderItemsFromList(orderItems);
        } catch (Exception e) {
            log.error("Error setting order items", e);
            // Handle the error appropriately
        }
        
        return entity;
    }
}
