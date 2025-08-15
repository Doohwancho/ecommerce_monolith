package com.cho.ecommerce.domain.order.service;


import com.cho.ecommerce.domain.member.service.MemberService;
import com.cho.ecommerce.domain.order.domain.Order.OrderItem;
import com.cho.ecommerce.domain.order.dto.CreateOrderRequestDTO;
import com.cho.ecommerce.domain.order.dto.OrderCreatedDTO;
import com.cho.ecommerce.domain.order.dto.OrderItemsResponseDTO;
import com.cho.ecommerce.domain.order.entity.DenormalizedOrderEntity;
import com.cho.ecommerce.domain.order.mapper.OrderMapper;
import com.cho.ecommerce.domain.order.repository.OrderRepository;
import com.cho.ecommerce.domain.product.domain.Product;
import com.cho.ecommerce.domain.product.service.ProductService;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final MemberService memberService;
    private final ProductService productService;
    
    public List<OrderItemsResponseDTO> getOrderItemsByUsername(String username) {
        List<DenormalizedOrderEntity> orders = orderRepository.findOrdersByMemberName(username);
        
        return orders.stream()
            .map(orderMapper::convertToOrderItemsResponseDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public OrderCreatedDTO createOrder(CreateOrderRequestDTO createOrderRequestDTO) {
        // 1. Validate the user exists
        if (!memberService.userExists(createOrderRequestDTO.getMemberId())) {
            throw new RuntimeException("User with id " + createOrderRequestDTO.getMemberId() + " not found"); //TODO - custom exception class 만들어서 도입
        }
    
        // 2. Validate base_price and discounts on productItems are valid
        // 3. validate if stock of requested product has plenty of quantity
        validateOrderItems(createOrderRequestDTO.getOrderItems());
        
        // 4. decrease stock quantity
        for (OrderItem item : createOrderRequestDTO.getOrderItems()) {
            productService.decreaseStock(item.getProductName(), item.getQuantity());
        }
        
        //TODO - 기존에 해당 유저의 order을 가져와서, totalPrice, totalQuantity 업데이트 후, orderItems를 추가하는 코드 필요
        
        // 5. save order
        DenormalizedOrderEntity orderEntity = orderMapper.convertToEntity(createOrderRequestDTO);
        DenormalizedOrderEntity savedOrder = orderRepository.save(orderEntity);
        
        // 6. return minimal information needed indicating POST /orders request was successful
        return new OrderCreatedDTO(savedOrder.getOrderId(), savedOrder.getOrderStatus());
    }
    
    private void validateOrderItems(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            int requestedQuantity = item.getQuantity();
            double requestedBasePrice = item.getBasePrice();
            double requestedDiscountedPrice = item.getDiscountedPrice();
            
            Product product = productService.getProductByName(item.getProductName());
            
            if (product == null) {
                throw new RuntimeException("Product " + item.getProductName() + " not found"); //TODO - custom exception class 만들어서 도입
            }
            
            if(requestedQuantity > product.getTotalQuantity()) {
                throw new RuntimeException("Product " + item.getProductName() + "'s stock is less than requested quantity"); //TODO - custom exception class 만들어서 도입
            }
            
            if(requestedBasePrice != product.getBasePrice() ||
                Math.abs(requestedBasePrice - product.getBasePrice()) >= 0.01
            ) {
                throw new RuntimeException("Invalid base price for product " + item.getProductName()); //TODO - custom exception class 만들어서 도입
            }
            
            if(requestedDiscountedPrice != product.getLowestPrice() ||
                Math.abs(requestedDiscountedPrice - product.getLowestPrice()) >= 0.01
            ) {
                throw new RuntimeException("Invalid discounted price for product " + item.getProductName()); //TODO - custom exception class 만들어서 도입
            }
        }
    }
}