package com.cho.ecommerce.domain.order.service;

import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.service.UserService;
import com.cho.ecommerce.domain.order.entity.CartEntity;
import com.cho.ecommerce.domain.order.repository.CartRepository;
import com.cho.ecommerce.domain.order.repository.OrderRepository;
import com.cho.ecommerce.domain.product.entity.ProductOptionVariationEntity;
import com.cho.ecommerce.domain.product.service.ProductOptionVariationService;
import com.cho.ecommerce.global.error.exception.business.InvalidCartRequestException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductOptionVariationService productOptionVariationService;
    
    @Transactional
    public List<CartEntity> createCartItems(List<com.cho.ecommerce.api.domain.CartRequestDTO> cartRequests) {
        //1. validation check
        if(cartRequests != null || cartRequests.size() == 0)
            throw new InvalidCartRequestException("요청하신 cartItems가 존재하지 않습니다.");
        
        //2. get User
        Long userId = cartRequests.get(0).getMemberId();
        UserEntity user = userService.findUserById(userId);
    
        //3. get Product Option Variation
        Long productOptionVariationId = cartRequests.get(0).getProductOptionVariationId();
        ProductOptionVariationEntity productOptionVariation = productOptionVariationService.getProductOptionVariationById(productOptionVariationId);
    
        //4. create list of cartEntities
        List<CartEntity> cartItems = new ArrayList<>();
        
        //5. add individual cartItem to cartItems
        for(com.cho.ecommerce.api.domain.CartRequestDTO request : cartRequests) {
            CartEntity cartEntity = new CartEntity();
            cartEntity.setMember(user);
            cartEntity.setProductOptionVariation(productOptionVariation);
            cartEntity.setAddedDate(LocalDateTime.now());
            cartEntity.setQuantity(request.getQuantity());
            
            cartItems.add(cartEntity);
        }
    
        //6. save list of cartItems
        return cartRepository.saveAll(cartItems);
    }
    
}
