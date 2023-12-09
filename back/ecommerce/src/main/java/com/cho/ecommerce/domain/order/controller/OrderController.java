package com.cho.ecommerce.domain.order.controller;

import com.cho.ecommerce.api.OrderApi;
import com.cho.ecommerce.api.domain.OrderDTO;
import com.cho.ecommerce.domain.order.adapter.OrderAdapter;
import com.cho.ecommerce.domain.order.service.OrderService;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@AllArgsConstructor
@RestController
public class OrderController implements OrderApi {
    
    private final OrderService orderService;
    private final OrderAdapter orderAdapter;
    
    
    @Override
    //TODO - 모든 controller method에 @PreAuthorize 하는 방식 말고, SecurityConfig에서 설정하는 방법 찾기
    public ResponseEntity<List<OrderDTO>> ordersGet() {
        List<OrderDTO> orders = orderAdapter.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
    
    @Override
    public ResponseEntity<Void> ordersOrderIdDelete(Long orderId) {
        orderService.deleteOrder(orderId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @Override
    public ResponseEntity<OrderDTO> ordersOrderIdGet(Long orderId) {
        OrderDTO order = orderAdapter.getOrderByIdForOrderDTO(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
    
    @Override
    public ResponseEntity<OrderDTO> ordersOrderIdPut(Long orderId, @Valid OrderDTO order) {
        OrderDTO updatedOrder = orderAdapter.updateOrder(orderId, order);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }
    
    @Override
    public ResponseEntity<OrderDTO> ordersPost(@Valid OrderDTO order) {
        OrderDTO createdOrder = orderAdapter.createOrder(order);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }
    
    @Override
    public ResponseEntity<List<com.cho.ecommerce.api.domain.OrderItemDetailsResponseDTO>> getOrderItemDetailsByUsername(
        @PathVariable String username) {
        List<com.cho.ecommerce.api.domain.OrderItemDetailsResponseDTO> orderItemDetails = orderAdapter.findOrderItemDetailsByUsername(
            username);
        if (orderItemDetails.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orderItemDetails);
    }
    
    @Override
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<com.cho.ecommerce.api.domain.OrderSalesStatisticsResponseDTO>> getMaxSalesProductAndAverageRatingAndTotalSalesPerCategoryDuringSixMonths() {
        List<com.cho.ecommerce.api.domain.OrderSalesStatisticsResponseDTO> orderSalesStatisticsResponseDTOS = orderService.findMaxSalesProductAndAverageRatingAndTotalSalesPerCategoryDuringSixMonths(); //TODO - Q. 다른 곳에서 재사용 안할 것 같은데, adaptor로 굳이 뺄 필요가 있을까?
        
        if(orderSalesStatisticsResponseDTOS.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(orderSalesStatisticsResponseDTOS);
    }
}
