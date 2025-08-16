package com.ecommerce.monolith.domain.order.service;


import com.ecommerce.monolith.domain.member.service.MemberService;
import com.ecommerce.monolith.domain.order.domain.Order.OrderItem;
import com.ecommerce.monolith.domain.order.dto.CreateOrderRequestDTO;
import com.ecommerce.monolith.domain.order.dto.OrderCreatedDTO;
import com.ecommerce.monolith.domain.order.dto.OrderItemsResponseDTO;
import com.ecommerce.monolith.domain.order.entity.DenormalizedOrderEntity;
import com.ecommerce.monolith.domain.order.mapper.OrderMapper;
import com.ecommerce.monolith.domain.order.repository.OrderRepository;
import com.ecommerce.monolith.domain.product.domain.Product;
import com.ecommerce.monolith.domain.product.service.ProductService;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
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
    
    /**
     * 결제 실패에 대한 보상 트랜잭션을 처리하는 메서드
     */
    @Transactional
    public void processPaymentFailure(Long orderId) {
        log.info("Processing payment failure for orderId: {}", orderId);
        
        // Optional<DenormalizedOrderEntity>를 사용하여 더 안전하게 처리
        DenormalizedOrderEntity order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId)); //TODO - custom exception
        
        // 멱등성 체크
        if (!"PENDING".equals(order.getOrderStatus())) {
            log.warn("Order [{}] is not in PENDING state. Skipping.", orderId);
            return;
        }
        
        // 1. 주문 상태 변경
        order.setOrderStatus("PAYMENT_FAILED");
        // @Transactional의 dirty checking으로 인해 save 호출은 선택사항
        
        // 2. 재고 원복
        try {
            order.getOrderItemsAsList().forEach(item -> {
                productService.increaseStock(item.getProductName(), item.getQuantity());
            });
        } catch (IOException e) {
            // 예외를 다시 던져서 트랜잭션 전체를 롤백
            throw new RuntimeException("Failed to parse order items for restocking", e);
        }
    }
    
    /**
     * 결제 취소에 대한 보상 트랜잭션을 처리하는 메서드
     */
    @Transactional
    public void processPaymentCancellation(Long orderId) {
        log.info("Processing payment cancellation for orderId: {}", orderId);
        
        DenormalizedOrderEntity order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId)); //TODO - custom exception
        
        // 멱등성 체크: 이미 처리된 건이거나, 처리할 수 없는 상태라면 중복 실행 방지
        if (!"PENDING".equals(order.getOrderStatus())) {
            log.warn("Order [{}] is not in PENDING state. Skipping.", orderId);
            return;
        }
        
        // 1. 주문 상태를 'CANCELLED'로 변경
        order.setOrderStatus("CANCELLED");
        
        // 2. 재고 원복
        try {
            order.getOrderItemsAsList().forEach(item -> {
                productService.increaseStock(item.getProductName(), item.getQuantity());
            });
        } catch (IOException e) {
            // 예외를 다시 던져서 트랜잭션 전체(주문 상태 변경 포함)를 롤백
            throw new RuntimeException("Failed to parse order items for restocking during cancellation", e);
        }
    }
}
