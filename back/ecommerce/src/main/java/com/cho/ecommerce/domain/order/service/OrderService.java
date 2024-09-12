package com.cho.ecommerce.domain.order.service;

import com.cho.ecommerce.api.domain.OrderDTO;
import com.cho.ecommerce.domain.member.domain.User;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.service.UserService;
import com.cho.ecommerce.domain.order.domain.Order;
import com.cho.ecommerce.domain.order.domain.OrderItemDetails;
import com.cho.ecommerce.domain.order.entity.OrderEntity;
import com.cho.ecommerce.domain.order.entity.OrderItemEntity;
import com.cho.ecommerce.domain.order.entity.nativeQuery.OrderSalesStatisticsInterface;
import com.cho.ecommerce.domain.order.mapper.OrderMapper;
import com.cho.ecommerce.domain.order.mapper.TimeMapper;
import com.cho.ecommerce.domain.order.repository.OrderRepository;
import com.cho.ecommerce.domain.product.domain.Discount;
import com.cho.ecommerce.domain.product.domain.DiscountType;
import com.cho.ecommerce.domain.product.domain.Product;
import com.cho.ecommerce.domain.product.entity.DiscountEntity;
import com.cho.ecommerce.domain.product.entity.OptionVariationEntity;
import com.cho.ecommerce.domain.product.entity.ProductItemEntity;
import com.cho.ecommerce.domain.product.entity.ProductOptionVariationEntity;
import com.cho.ecommerce.domain.product.service.DiscountService;
import com.cho.ecommerce.domain.product.service.OptionService;
import com.cho.ecommerce.domain.product.service.ProductItemService;
import com.cho.ecommerce.domain.product.service.ProductService;
import com.cho.ecommerce.global.error.exception.business.BusinessException;
import com.cho.ecommerce.global.error.exception.business.OrderItemsNotIncluded;
import com.cho.ecommerce.global.error.exception.business.OrderRequestByInvalidUser;
import com.cho.ecommerce.global.error.exception.business.RequestedOrderQuantityExceedsCurrentStock;
import com.cho.ecommerce.global.error.exception.business.ResourceNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private TimeMapper timeMapper;
    
    @Autowired
    private UserService userService;
    @Autowired
    private OptionService optionService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductItemService productItemService;
    @Autowired
    private DiscountService discountService;
    
    @Transactional
    public OrderEntity createOrder(
        List<com.cho.ecommerce.api.domain.OrderRequestDTO> orderRequests) {
        //1. validation check
        //1-1. check if orderItems are not empty
        if (orderRequests.isEmpty()) {
            throw new OrderItemsNotIncluded("주문 요청을 신청하였으나, 주문에 상품을 하나도 포함하지 않았습니다.");
        }
        
        UserEntity user = userService.findUserById(orderRequests.get(0).getMemberId());
        
        try {
            //1-2. check if all orderRequests are requested by the same userId
            Order.checkAllOrderItemsFromSameUser(orderRequests);
            
            //1-3. check if requested user's account is not locked
            //TODO - Q. user validation 처리 후 invalid시 invalidate user session + lock처리를 domain 객체 내부에서 하면, 도메인 객체 내부에 @Autowired Service; 받아야 하는데, 이 구조가 맞는걸까? 아니면 비즈니스 코드에 이렇게 풀어서 쓰고 주석 다는게 더 좋은 구조일까?
            if (User.isLockedUser(user) == true) {
                throw new OrderRequestByInvalidUser("주문이 잠긴 계정 유저로부터 주문이 요청되었습니다.");
            }
            
        } catch (BusinessException e) {
            //1-4. if request is rigged, invalidate his session and lock the user
            userService.invalidateUserSessionAndLockUser(user);
            throw e;
        }
        
        //2. create order
        OrderEntity order = new OrderEntity();
        
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus("PROCESSING");
        order.setMember(user);
        order.setOrderItems(new HashSet<>());
        
        //3. create orderItems in order to place them in orderEntity
        for (com.cho.ecommerce.api.domain.OrderRequestDTO orderRequest : orderRequests) {
            //3-1. create discount
            DiscountType discountType = null;
            String discountTypeStr = orderRequest.getDiscountType();
            
            switch (discountTypeStr) {
                case "PERCENTAGE":
                    discountType = DiscountType.PERCENTAGE;
                    break;
                case "FLAT_RATE":
                    discountType = DiscountType.FLAT_RATE;
                    break;
                default:
                    break;
            }
            
            //builder pattern에서 객체 생성시 자체적으로 validation check한다.
            Discount discount = new Discount.Builder()
                .discountId(orderRequest.getDiscountId())
                .discountType(discountType)
                .discountValue(orderRequest.getDiscountValue())
                .startDate(orderRequest.getStartDate())
                .endDate(orderRequest.getEndDate())
                .build();
            
            //3-2. compare discount with actual discount saved on db
            DiscountEntity discountEntityFromDb = discountService.getDiscountById(
                orderRequest.getDiscountId());
            
            try {
                discount.validateRequestedDiscountWithSavedDiscountEntity(discountEntityFromDb);
            } catch (BusinessException e) {
                //if user requested discount is rigged, lock the user
                userService.invalidateUserSessionAndLockUser(user);
                throw e;
            }
            
            //3-3. create productItem to validate requested quantity is valid
            ProductItemEntity productItem = productItemService.getById(
                orderRequest.getProductItemId());
            
            if (orderRequest.getOrderQuantity() > productItem.getQuantity()) {
                throw new RequestedOrderQuantityExceedsCurrentStock("요청하신 주문 갯수가 현재 재고수량을 초과했습니다.");
            }
            
            //3-4. subtract quantity from ProductItemEntity
            productItem.setQuantity(productItem.getQuantity() - orderRequest.getOrderQuantity());
            
            //3-5. get discountedPrice from Product
            Double discountedPrice = Product.getDiscountedPrice(productItem.getPrice(),
                new ArrayList<>(Collections.singletonList(discount)));
            
            //3-6. create product_option_variation
            ProductOptionVariationEntity productOptionVariation = new ProductOptionVariationEntity();
            
            OptionVariationEntity optionVariation = optionService.getOptionVariationById(
                orderRequest.getOptionVariationId());
            productOptionVariation.setOptionVariation(optionVariation);
            productOptionVariation.setProductItem(productItem);
            
            //3-7. create orderItems
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setProductOptionVariation(productOptionVariation);
            orderItemEntity.setOrder(order);
            orderItemEntity.setQuantity(orderRequest.getOrderQuantity());
            orderItemEntity.setPrice(discountedPrice); //calculate discounted price
            
            //yet - delivery 처리
            
            //yet - payment 처리
            
            //3-8. add orderItems to order
            order.getOrderItems().add(orderItemEntity);
        }
        
        //4. save order (cascade-all to save orderItems, product_option_variation, product_item as well)
        return orderRepository.save(order);
    }
    
    public OrderEntity getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(
            () -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }
    
    public List<OrderItemDetails> findOrderItemDetailsByUsername(String username) {
        return orderRepository.getOrderItemDetailsByUsername(
            username).orElseThrow(() -> new ResourceNotFoundException(
            "No order details found for username: " + username));
    }
    
    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public OrderEntity updateOrder(Long orderId, OrderDTO orderDetails) {
        //1. check whether order exists
        OrderEntity orderEntity = getOrderById(orderId);
        
        //TODO - what to update?
        //2. update order status
        orderEntity.setOrderStatus(orderDetails.getOrderStatus());
        
        //3. save order
        return orderRepository.save(orderEntity);
    }
    
    public void deleteOrder(Long orderId) {
        OrderEntity order = getOrderById(orderId);
        orderRepository.delete(order);
    }
    
    public List<com.cho.ecommerce.api.domain.OrderSalesStatisticsResponseDTO> findMaxSalesProductAndAverageRatingAndTotalSalesPerCategoryDuringLastNMonths(
        Long numberOfMonthsForProductStatistics) {
        //step1) 현재 날짜와 N-개월 전 날짜 구하기
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(numberOfMonthsForProductStatistics);
        
        //step2) 날짜를 String 형식으로 포맷 (yyyy-MM-dd)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedStartDate = startDate.format(formatter);
        String formattedEndDate = endDate.format(formatter);
        
        //step3) run query
        List<OrderSalesStatisticsInterface> queryResults = orderRepository.findMaxSalesProductAndAverageRatingAndTotalSalesPerCategoryDuringLastNMonths(
            formattedStartDate, formattedEndDate);
        
        //step4) query한걸 response DTO에 정제하여 반환하기
        List<com.cho.ecommerce.api.domain.OrderSalesStatisticsResponseDTO> list = new ArrayList<>();
        
        queryResults.forEach(result -> {
            com.cho.ecommerce.api.domain.OrderSalesStatisticsResponseDTO dto = new com.cho.ecommerce.api.domain.OrderSalesStatisticsResponseDTO();
            dto.setCategoryId(result.getCategoryId());
            dto.setCategoryName(result.getCategoryName());
            dto.setNumberOfProductsPerCategory(result.getNumberOfProductsPerCategory());
            dto.setAverageRating(result.getAverageRating());
            dto.setTotalSalesPerCategory(result.getTotalSalesPerCategory());
            dto.setProductId(result.getProductId());
            dto.setTopSalesProductName(result.getTopSalesProductName());
            dto.setTopSalesOfProduct(result.getTopSalesOfProduct());
            
            list.add(dto);
        });
        
        return list;
    }
}
