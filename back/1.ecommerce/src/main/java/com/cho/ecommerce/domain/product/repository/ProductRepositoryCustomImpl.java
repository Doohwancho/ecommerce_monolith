package com.cho.ecommerce.domain.product.repository;

import com.cho.ecommerce.domain.product.entity.ProductEntity;
import com.cho.ecommerce.domain.product.entity.QCategoryEntity;
import com.cho.ecommerce.domain.product.entity.QDiscountEntity;
import com.cho.ecommerce.domain.product.entity.QOptionEntity;
import com.cho.ecommerce.domain.product.entity.QOptionVariationEntity;
import com.cho.ecommerce.domain.product.entity.QProductEntity;
import com.cho.ecommerce.domain.product.entity.QProductItemEntity;
import com.cho.ecommerce.domain.product.entity.QProductOptionVariationEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    @Autowired
    public ProductRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
    
    @Override
    public List<com.cho.ecommerce.api.domain.ProductWithOptionsDTO> findAllProductsByCategory(
        Long categoryId) {
        QCategoryEntity category = QCategoryEntity.categoryEntity;
        QOptionEntity option = QOptionEntity.optionEntity;
        QOptionVariationEntity optionVariation = QOptionVariationEntity.optionVariationEntity;
        QProductEntity product = QProductEntity.productEntity;
        QProductItemEntity productItem = QProductItemEntity.productItemEntity;
        QProductOptionVariationEntity productOptionVariation = QProductOptionVariationEntity.productOptionVariationEntity;
        
        List<Tuple> results = queryFactory.select(
                product.productId,
                product.name,
                product.description,
                product.rating,
                product.ratingCount,
                category.categoryId,
                category.name,
                option.optionId,
                option.value,
                optionVariation.value,
                productItem.quantity,
                productItem.price)
            .from(product)
            .join(product.category,
                category) // Assuming a direct association between Product and Category
            .join(product.productItems,
                productItem) // Assuming Product has a collection of ProductItems
            .join(productItem.productOptionVariations,
                productOptionVariation) // Assuming ProductItem has a collection of ProductOptionVariations
            .join(productOptionVariation.optionVariation,
                optionVariation) // Assuming ProductOptionVariation has an OptionVariation
            .join(optionVariation.option, option) // Assuming OptionVariation has an Option
            .where(category.categoryId.eq(categoryId))
            .orderBy(product.productId.asc())
            .fetch();
        
        List<com.cho.ecommerce.api.domain.ProductWithOptionsDTO> productWithOptionsDTOList = new ArrayList<>();
        for (Tuple tuple : results) {
            com.cho.ecommerce.api.domain.ProductWithOptionsDTO dto = new com.cho.ecommerce.api.domain.ProductWithOptionsDTO();
            dto.setProductId(tuple.get(product.productId));
            dto.setName(tuple.get(product.name));
            dto.setDescription(tuple.get(product.description));
            dto.setRating(tuple.get(product.rating));
            dto.setRatingCount(tuple.get(product.ratingCount));
            dto.setCategoryId(tuple.get(category.categoryId));
            dto.setCategoryName(tuple.get(category.name));
            dto.setOptionId(tuple.get(option.optionId));
            dto.setOptionName(tuple.get(option.value));
            dto.setOptionVariationName(tuple.get(optionVariation.value));
            dto.setQuantity(tuple.get(productItem.quantity));
            dto.setPrice(tuple.get(productItem.price));
            
            productWithOptionsDTOList.add(dto);
        }
        
        return productWithOptionsDTOList;
    }
    
    @Override
    public List<com.cho.ecommerce.api.domain.ProductWithOptionsVer2DTO> findAllProductsByCategoryVer2(
        Long categoryId) {
        QCategoryEntity category = QCategoryEntity.categoryEntity;
        QProductEntity product = QProductEntity.productEntity;
        QProductItemEntity productItem = QProductItemEntity.productItemEntity;
        QProductOptionVariationEntity productOptionVariation = QProductOptionVariationEntity.productOptionVariationEntity;
        QOptionVariationEntity optionVariation = QOptionVariationEntity.optionVariationEntity;
        
        return queryFactory.select(
                Projections.bean(com.cho.ecommerce.api.domain.ProductWithOptionsVer2DTO.class,
                    category.categoryId,
                    product.productId,
                    product.name.as("productName"),
                    product.description,
                    product.rating,
                    product.ratingCount,
                    productItem.productItemId,
                    productItem.price,
                    optionVariation.optionVariationId,
                    optionVariation.value.as("optionVariationName")
                ))
            .from(category)
            .join(product).on(category.categoryId.eq(product.category.categoryId))
            .leftJoin(productItem).on(product.productId.eq(productItem.product.productId))
            .leftJoin(productOptionVariation)
            .on(productOptionVariation.productItem.productItemId.eq(productItem.productItemId))
            .leftJoin(optionVariation).on(optionVariation.optionVariationId.eq(
                productOptionVariation.optionVariation.optionVariationId))
            .where(category.categoryId.eq(categoryId))
            .fetch();
    }
    
    @Override
    public Optional<ProductEntity> findProductDetailDTOsById(Long productId) {
        QProductEntity qProduct = QProductEntity.productEntity;
        QCategoryEntity qCategory = QCategoryEntity.categoryEntity;
        QProductItemEntity qProductItem = QProductItemEntity.productItemEntity;
        QDiscountEntity qDiscount = QDiscountEntity.discountEntity;
        QProductOptionVariationEntity qProductOptionVariation = QProductOptionVariationEntity.productOptionVariationEntity;
        
        ProductEntity product = queryFactory
            .selectDistinct(qProduct)
            .from(qProduct)
            .leftJoin(qProduct.category, qCategory).fetchJoin()
            .leftJoin(qProduct.productItems, qProductItem).fetchJoin()
            .leftJoin(qProductItem.discounts, qDiscount).fetchJoin()
            .leftJoin(qProductItem.productOptionVariations, qProductOptionVariation).fetchJoin()
            .where(qProduct.productId.eq(productId))
            .fetchOne();
        
        return Optional.ofNullable(product);
    }
    
    @Override
    @Cacheable("topTenRatedProductsCached")
    public List<ProductEntity> findProductsByIdIn(List<Long> productIds) {
        QProductEntity product = QProductEntity.productEntity;
        
        return queryFactory
            .selectFrom(product)
            .where(product.productId.in(productIds))
            .fetch();
    }
}