package com.cho.ecommerce.domain.order.controller;

import com.cho.ecommerce.api.OrderApi;
import com.cho.ecommerce.api.domain.OrderDTO;
import com.cho.ecommerce.api.domain.OrderItemDetailsDTO;
import com.cho.ecommerce.domain.order.service.OrderService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class OrderController implements OrderApi {
    
    private final OrderService orderService;
    
    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    //TODO - 모든 controller method에 @PreAuthorize 하는 방식 말고, SecurityConfig에서 설정하는 방법 찾기
    public ResponseEntity<List<OrderDTO>> ordersGet() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
    
    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<Void> ordersOrderIdDelete(Long orderId) {
        orderService.deleteOrder(orderId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<OrderDTO> ordersOrderIdGet(Long orderId) {
        OrderDTO order = orderService.getOrderByIdForOrderDTO(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
    
    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<OrderDTO> ordersOrderIdPut(Long orderId, OrderDTO order) {
        OrderDTO updatedOrder = orderService.updateOrder(orderId, order);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }
    
    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<OrderDTO> ordersPost(OrderDTO order) {
        OrderDTO createdOrder = orderService.createOrder(order);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }
    
    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<List<OrderItemDetailsDTO>> getOrderItemDetailsByUsername(
        @PathVariable String username) {
        List<OrderItemDetailsDTO> orderItemDetails = orderService.findOrderItemDetailsByUsername(
            username);
        if (orderItemDetails.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orderItemDetails);
    }
    
}
