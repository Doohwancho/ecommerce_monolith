package com.cho.ecommerce.domain.order.repository;

import com.cho.ecommerce.domain.member.entity.QUserEntity;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.order.domain.OrderItemDetails;
import com.cho.ecommerce.domain.order.entity.OrderEntity;
import com.cho.ecommerce.domain.order.entity.OrderItemEntity;
import com.cho.ecommerce.domain.order.entity.QOrderEntity;
import com.cho.ecommerce.domain.order.entity.QOrderItemEntity;
import com.cho.ecommerce.domain.product.entity.OptionEntity;
import com.cho.ecommerce.domain.product.entity.OptionVariationEntity;
import com.cho.ecommerce.domain.product.entity.ProductEntity;
import com.cho.ecommerce.domain.product.entity.ProductItemEntity;
import com.cho.ecommerce.domain.product.entity.QOptionEntity;
import com.cho.ecommerce.domain.product.entity.QOptionVariationEntity;
import com.cho.ecommerce.domain.product.entity.QProductEntity;
import com.cho.ecommerce.domain.product.entity.QProductItemEntity;
import com.cho.ecommerce.domain.product.entity.QProductOptionVariationEntity;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    @Autowired
    public OrderRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
    
    
    @Override
    public Optional<List<OrderItemDetails>> getOrderItemDetailsByUsername(String username) {
        QOrderEntity order = QOrderEntity.orderEntity;
        QUserEntity member = QUserEntity.userEntity;
        QOrderItemEntity orderItem = QOrderItemEntity.orderItemEntity;
        QProductOptionVariationEntity productOptionVariation = QProductOptionVariationEntity.productOptionVariationEntity;
        QProductItemEntity productItem = QProductItemEntity.productItemEntity;
        QProductEntity product = QProductEntity.productEntity;
        QOptionEntity option = QOptionEntity.optionEntity;
        QOptionVariationEntity optionVariation = QOptionVariationEntity.optionVariationEntity;
        
        List<Tuple> queryResult = queryFactory
            .select(order, member, orderItem, productOptionVariation, productItem, product, option,
                optionVariation)
            .from(orderItem)
            .join(orderItem.order, order)
            .join(order.member, member)
            .join(orderItem.productOptionVariation, productOptionVariation)
            .join(productOptionVariation.productItem, productItem)
            .join(productItem.product, product)
            .join(productOptionVariation.optionVariation, optionVariation)
            .join(optionVariation.option, option)
            .where(member.username.eq(username))
            .fetch();
        
        List<OrderItemDetails> result = queryResult.stream()
            .map(tuple -> {
                OrderEntity orderEntity = tuple.get(order);
                UserEntity userEntity = tuple.get(member);
                OrderItemEntity orderItemEntity = tuple.get(orderItem);
                ProductItemEntity productItemEntity = tuple.get(productItem);
                ProductEntity productEntity = tuple.get(product);
                OptionEntity optionEntity = tuple.get(option);
                OptionVariationEntity optionVariationEntity = tuple.get(optionVariation);
                
                return new OrderItemDetails(
                    orderItemEntity.getOrderItemId(),
                    orderEntity.getOrderId(),
                    orderEntity.getOrderDate(),
                    orderEntity.getOrderStatus(),
                    userEntity.getMemberId(),
                    userEntity.getUsername(),
                    userEntity.getEmail(),
                    userEntity.getName(),
                    userEntity.getRole(),
                    userEntity.isEnabled(),
                    userEntity.getCreated(),
                    userEntity.getUpdated(),
                    productEntity.getProductId(),
                    productEntity.getName(),
                    productEntity.getDescription(),
                    productEntity.getRating(),
                    productEntity.getRatingCount(),
                    optionEntity.getValue(),
                    optionVariationEntity.getValue(),
                    productItemEntity.getQuantity(),
                    productItemEntity.getPrice()
                );
            })
            .collect(Collectors.toList());
        return Optional.ofNullable(queryResult.isEmpty() ? null : result);
    }
}
