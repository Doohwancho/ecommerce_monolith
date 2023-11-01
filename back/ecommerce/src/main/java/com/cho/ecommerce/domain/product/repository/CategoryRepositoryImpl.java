package com.cho.ecommerce.domain.product.repository;

import com.cho.ecommerce.domain.product.entity.CategoryEntity;
import com.cho.ecommerce.domain.product.entity.QCategoryEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryRepositoryImpl implements CategoryRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    private final QCategoryEntity qCategoryEntity = QCategoryEntity.categoryEntity;
    
    @Autowired
    public CategoryRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
    
    @Override
    public CategoryEntity findByCategoryId(Long categoryId) {
        return queryFactory
            .selectFrom(qCategoryEntity)
            .where(qCategoryEntity.categoryId.eq(categoryId))
            .fetchOne();
    }
}

