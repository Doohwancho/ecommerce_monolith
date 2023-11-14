package com.cho.ecommerce.domain.product.repository;

import com.cho.ecommerce.domain.product.entity.OptionEntity;
import com.cho.ecommerce.domain.product.entity.QOptionEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

public class OptionRepositoryImpl implements OptionRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    @Autowired
    public OptionRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
    
    @Override
    public List<OptionEntity> findByCategory_CategoryId(Long categoryId) {
        
        QOptionEntity option = QOptionEntity.optionEntity;
        return queryFactory.selectFrom(option)
            .where(option.category.categoryId.eq(categoryId))
            .fetch();
    }
}

