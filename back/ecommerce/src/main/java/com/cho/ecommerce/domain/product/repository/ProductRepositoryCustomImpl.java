package com.cho.ecommerce.domain.product.repository;

import com.cho.ecommerce.domain.product.entity.ProductEntity;
import com.cho.ecommerce.domain.product.entity.QCategoryEntity;
import com.cho.ecommerce.domain.product.entity.QDiscountEntity;
import com.cho.ecommerce.domain.product.entity.QProductEntity;
import com.cho.ecommerce.domain.product.entity.QProductItemEntity;
import com.cho.ecommerce.domain.product.entity.QProductOptionVariationEntity;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    @Autowired
    public ProductRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
    
    @Override
    public List<ProductEntity> findAllProductsByCategory(Long categoryId) {
        QProductEntity product = QProductEntity.productEntity;
        QCategoryEntity category = QCategoryEntity.categoryEntity;
    
        List<ProductEntity> result = queryFactory
            .selectFrom(product)
            .join(product.category, category)
            .where(category.categoryId.eq(categoryId))
            .fetch();
        
        return result;
    }
    
    @Override
    public List<ProductEntity> findProductDetailDTOsById(Long productId) {
        QProductEntity qProduct = QProductEntity.productEntity;
        QCategoryEntity qCategory = QCategoryEntity.categoryEntity;
//        QOptionEntity qOption = QOptionEntity.optionEntity;
//        QOptionVariationEntity qOptionVariation = QOptionVariationEntity.optionVariationEntity;
        QProductItemEntity qProductItem = QProductItemEntity.productItemEntity;
        QProductOptionVariationEntity qProductOptionVariation = QProductOptionVariationEntity.productOptionVariationEntity;
        QDiscountEntity qDiscount = QDiscountEntity.discountEntity;
        
        JPAQuery<ProductEntity> query = queryFactory.selectDistinct(qProduct)
            .from(qProduct)
            .leftJoin(qProduct.category, qCategory).fetchJoin()
            // Only join single-valued associations with fetchJoin
            .leftJoin(qProduct.productItems, qProductItem).fetchJoin()
            // For collections, consider using simple joins and fetching them in separate queries if needed
            .leftJoin(qProductItem.productOptionVariations, qProductOptionVariation)
            .leftJoin(qProductItem.discounts, qDiscount)
            .where(qProduct.productId.eq(productId)); // If you're looking for a specific product
        
        // Fetch the results
        List<ProductEntity> products = query.fetch();
        
        return products;
    }
}