package com.cho.ecommerce.domain.order.mapper;


import com.cho.ecommerce.api.domain.OrderDTO;
import com.cho.ecommerce.domain.order.domain.Order;
import com.cho.ecommerce.domain.order.entity.OrderEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderEntity orderToOrderEntity(Order order);
    
    Order orderEntityToOrder(OrderEntity orderEntity);
    
    
    OrderDTO orderEntityToOrderDTO(OrderEntity orderEntity);
    
    OrderEntity orderDTOToOrderEntity(OrderDTO orderDTO);
}
