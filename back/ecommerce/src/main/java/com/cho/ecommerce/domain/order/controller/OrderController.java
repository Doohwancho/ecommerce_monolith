package com.cho.ecommerce.domain.order.controller;

import com.cho.ecommerce.api.OrderApi;
import com.cho.ecommerce.api.domain.OrderDTO;
import com.cho.ecommerce.domain.order.service.OrderService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class OrderController implements OrderApi {
    private final OrderService orderService;
    
    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @Override
    public ResponseEntity<List<OrderDTO>> ordersGet() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
    
    @Override
    public ResponseEntity<Void> ordersOrderIdDelete(Long orderId) {
        orderService.deleteOrder(orderId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @Override
    public ResponseEntity<OrderDTO> ordersOrderIdGet(Long orderId) {
        OrderDTO order = orderService.getOrderByIdForOrderDTO(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
    
    @Override
    public ResponseEntity<OrderDTO> ordersOrderIdPut(Long orderId, OrderDTO order) {
        OrderDTO updatedOrder = orderService.updateOrder(orderId, order);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }
    
    @Override
    public ResponseEntity<OrderDTO> ordersPost(OrderDTO order) {
        OrderDTO createdOrder = orderService.createOrder(order);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }
}
