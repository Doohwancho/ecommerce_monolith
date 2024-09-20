package com.cho.ecommerce.domain.order.controller;

import com.cho.ecommerce.api.OrderApi;
import com.cho.ecommerce.api.domain.OrderDTO;
import com.cho.ecommerce.domain.order.adapter.OrderAdapter;
import com.cho.ecommerce.domain.order.entity.OrderEntity;
import com.cho.ecommerce.domain.order.service.OrderService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> createOrder(@Valid List<com.cho.ecommerce.api.domain.OrderRequestDTO> orderRequest) {
        OrderEntity order = orderService.createOrder(orderRequest);
    
        if (order.getOrderId() != null) {
            return ResponseEntity.status(201).body("Ordered successfully");
        } else {
            // Handle the failure case appropriately
            return ResponseEntity.status(400).body("Order creation failed");
        }
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
    public ResponseEntity<List<com.cho.ecommerce.api.domain.OrderSalesStatisticsResponseDTO>> getMaxSalesProductAndAverageRatingAndTotalSalesPerCategoryDuringLastNMonths(Long numberOfMonthsForProductStatistics) {
        //step1) check whether given paramter is more than 3months(b/c fakedata only supports data within last 3 months)
        numberOfMonthsForProductStatistics = Math.min(numberOfMonthsForProductStatistics, 3);
        
        //step2) 통계쿼리 호출
        List<com.cho.ecommerce.api.domain.OrderSalesStatisticsResponseDTO> orderSalesStatisticsResponseDTOS = orderService.findMaxSalesProductAndAverageRatingAndTotalSalesPerCategoryDuringLastNMonths(numberOfMonthsForProductStatistics); //TODO - Q. 다른 곳에서 재사용 안할 것 같은데, adaptor로 굳이 뺄 필요가 있을까?
        
        //step3) validation 처리
        //TODO - 만약 요청한게 3보다 큰 숫자면, http response에 이를 알려야 한다.
        if(orderSalesStatisticsResponseDTOS.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        //step4) return http response
        return ResponseEntity.ok(orderSalesStatisticsResponseDTOS);
    }
}
