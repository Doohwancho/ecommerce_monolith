package com.ecommerce.monolith.domain.order.controller;


import com.ecommerce.monolith.domain.order.dto.CreateOrderRequestDTO;
import com.ecommerce.monolith.domain.order.dto.OrderCreatedDTO;
import com.ecommerce.monolith.domain.order.dto.OrderItemsResponseDTO;
import com.ecommerce.monolith.domain.order.service.OrderService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    
    private final OrderService orderService;
    
    @GetMapping("/orderItems/{username}")
    public ResponseEntity<List<OrderItemsResponseDTO>> getOrderItemsByUsername(@PathVariable String username) {
        List<OrderItemsResponseDTO> orderItems = orderService.getOrderItemsByUsername(username);
        return ResponseEntity.ok(orderItems);
    }
    
    @PostMapping
    public ResponseEntity<OrderCreatedDTO> createOrder(@RequestBody CreateOrderRequestDTO createOrderRequestDTO) {
        OrderCreatedDTO createdOrder = orderService.createOrder(createOrderRequestDTO);
        return ResponseEntity.ok(createdOrder);
    }
}