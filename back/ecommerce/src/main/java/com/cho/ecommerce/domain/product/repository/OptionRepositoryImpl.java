package com.cho.ecommerce.domain.product.repository;

import com.cho.ecommerce.domain.product.entity.OptionEntity;
import com.cho.ecommerce.domain.product.entity.QOptionEntity;
import com.cho.ecommerce.global.config.fakedata.FakeDataGenerator;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class OptionRepositoryImpl implements OptionRepositoryCustom {
    
    private final Logger log = LoggerFactory.getLogger(FakeDataGenerator.class);
    private final JPAQueryFactory queryFactory;
    
    @Autowired
    public OptionRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
    
    @Override
    public List<OptionEntity> findByCategory_CategoryId(Long categoryId) {
    
        QOptionEntity option = QOptionEntity.optionEntity;
        List<OptionEntity> result = queryFactory.selectFrom(option)
            .where(option.category.categoryId.eq(categoryId))
            .fetch();
    
        log.info("queryDSL이에오!");
        log.info(result.toString());
        
        return result;
    }
}

