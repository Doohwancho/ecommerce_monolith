package com.cho.ecommerce.domain.order.mapper;

import com.cho.ecommerce.domain.order.dto.OrderItemsResponseDTO;
import com.cho.ecommerce.domain.order.entity.DenormalizedOrderEntity;
import com.cho.ecommerce.global.config.parser.ObjectMapperUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
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
}
