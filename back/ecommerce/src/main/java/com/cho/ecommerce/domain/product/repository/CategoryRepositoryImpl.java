package com.cho.ecommerce.domain.product.repository;

import com.cho.ecommerce.domain.order.entity.QOrderEntity;
import com.cho.ecommerce.domain.order.entity.QOrderItemEntity;
import com.cho.ecommerce.domain.product.entity.CategoryEntity;
import com.cho.ecommerce.domain.product.entity.QCategoryEntity;
import com.cho.ecommerce.domain.product.entity.QProductEntity;
import com.cho.ecommerce.domain.product.entity.QProductItemEntity;
import com.cho.ecommerce.domain.product.entity.QProductOptionVariationEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryRepositoryImpl implements CategoryRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    @Autowired
    public CategoryRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
    
    @Override
    public CategoryEntity findByCategoryId(Long categoryId) {
        final QCategoryEntity qCategoryEntity = QCategoryEntity.categoryEntity;
        
        return queryFactory
            .selectFrom(qCategoryEntity)
            .where(qCategoryEntity.categoryId.eq(categoryId))
            .fetchOne();
    }
}

