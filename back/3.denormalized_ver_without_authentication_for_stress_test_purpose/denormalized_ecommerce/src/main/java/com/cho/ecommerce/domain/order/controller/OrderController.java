package com.cho.ecommerce.domain.order.controller;


import com.cho.ecommerce.domain.order.dto.OrderItemsResponseDTO;
import com.cho.ecommerce.domain.order.service.OrderService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}