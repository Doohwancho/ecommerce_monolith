package com.cho.ecommerce.domain.order.mapper;


import com.cho.ecommerce.api.domain.OrderDTO;
import com.cho.ecommerce.domain.order.domain.Order;
import com.cho.ecommerce.domain.order.entity.OrderEntity;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    
    default OffsetDateTime map(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atOffset(ZoneOffset.UTC) : null;
    }
    
    default LocalDateTime map(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null ? offsetDateTime.toLocalDateTime() : null;
    }
    
    OrderEntity orderToOrderEntity(Order order);
    
    Order orderEntityToOrder(OrderEntity orderEntity);
    
    
    OrderDTO orderEntityToOrderDTO(OrderEntity orderEntity);
    
    OrderEntity orderDTOToOrderEntity(OrderDTO orderDTO);
    
}
