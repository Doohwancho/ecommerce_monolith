package com.cho.ecommerce.domain.order.mapper;


import com.cho.ecommerce.api.domain.OrderDTO;
import com.cho.ecommerce.domain.order.domain.Order;
import com.cho.ecommerce.domain.order.entity.OrderEntity;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
    
    
    @Mapping(source = "member.memberId", target = "memberId")
    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "deliveryId", ignore = true)
    OrderDTO orderEntityToOrderDTO(OrderEntity orderEntity);
    
    @Mapping(target = "member", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    OrderEntity orderDTOToOrderEntity(OrderDTO orderDTO);
    
}
