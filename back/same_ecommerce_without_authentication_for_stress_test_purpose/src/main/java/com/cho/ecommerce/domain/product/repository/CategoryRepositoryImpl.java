package com.cho.ecommerce.domain.product.repository;

import com.cho.ecommerce.domain.order.entity.QOrderEntity;
import com.cho.ecommerce.domain.order.entity.QOrderItemEntity;
import com.cho.ecommerce.domain.product.entity.CategoryEntity;
import com.cho.ecommerce.domain.product.entity.QCategoryEntity;
import com.cho.ecommerce.domain.product.entity.QProductEntity;
import com.cho.ecommerce.domain.product.entity.QProductItemEntity;
import com.cho.ecommerce.domain.product.entity.QProductOptionVariationEntity;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
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
    
    @Override
    public List<CategoryEntity> findCategoriesByParentName(String parentName) {
        QCategoryEntity category = QCategoryEntity.categoryEntity;
        QCategoryEntity parentCategory = new QCategoryEntity("parentCategory");

        return queryFactory
            .select(category)
            .from(category)
            .leftJoin(parentCategory).on(category.parentCategoryId.eq(parentCategory.categoryId))
            .where(parentCategory.name.eq(parentName))
            .fetch();
    }
    
    @Override
    public List<CategoryEntity> findCategoriesByDepth(Integer depth) {
        final QCategoryEntity qCategoryEntity = QCategoryEntity.categoryEntity;
        
        return queryFactory.
            selectFrom(qCategoryEntity)
            .where(qCategoryEntity.depth
                .eq(depth))
            .fetch();
    }
    
    public List<com.cho.ecommerce.api.domain.AllCategoriesByDepthResponseDTO> findAllCategoriesSortByDepth() {
        final QCategoryEntity c1 = new QCategoryEntity("c1");
        final QCategoryEntity c2 = new QCategoryEntity("c2");
        final QCategoryEntity c3 = new QCategoryEntity("c3");
    
        List<Tuple> results = queryFactory.select(c1.categoryId, c1.name, c2.categoryId, c2.name,
                c3.categoryId, c3.name)
            .from(c1)
            .leftJoin(c2).on(c1.categoryId.eq(c2.parentCategoryId))
            .leftJoin(c3).on(c2.categoryId.eq(c3.parentCategoryId))
            .where(c1.depth.eq(0))
            .orderBy(c1.categoryId.asc(), c2.categoryId.asc(), c3.categoryId.asc())
            .fetch();

        List<com.cho.ecommerce.api.domain.AllCategoriesByDepthResponseDTO> responseList = new ArrayList<>();
        for (Tuple tuple : results) {
            com.cho.ecommerce.api.domain.AllCategoriesByDepthResponseDTO dto = new com.cho.ecommerce.api.domain.AllCategoriesByDepthResponseDTO();
            dto.setTopCategoryId(tuple.get(c1.categoryId));
            dto.setTopCategoryName(tuple.get(c1.name));
            dto.setMidCategoryId(tuple.get(c2.categoryId));
            dto.setMidCategoryName(tuple.get(c2.name));
            dto.setLowCategoryId(tuple.get(c3.categoryId));
            dto.setLowCategoryName(tuple.get(c3.name));
            responseList.add(dto);
        }
        return responseList;
    }

}

