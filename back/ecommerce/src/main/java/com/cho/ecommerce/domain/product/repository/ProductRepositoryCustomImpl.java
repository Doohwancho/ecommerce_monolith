package com.cho.ecommerce.domain.product.repository;

import static com.google.common.base.Predicates.not;

import com.cho.ecommerce.domain.product.entity.ProductEntity;
import com.cho.ecommerce.domain.product.entity.QCategoryEntity;
import com.cho.ecommerce.domain.product.entity.QDiscountEntity;
import com.cho.ecommerce.domain.product.entity.QOptionEntity;
import com.cho.ecommerce.domain.product.entity.QOptionVariationEntity;
import com.cho.ecommerce.domain.product.entity.QProductEntity;
import com.cho.ecommerce.domain.product.entity.QProductItemEntity;
import com.cho.ecommerce.domain.product.entity.QProductOptionVariationEntity;
import com.cho.ecommerce.global.error.exception.business.ResourceNotFoundException;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
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
    public List<com.cho.ecommerce.api.domain.ProductWithOptionsDTO> findAllProductsByCategory(Long categoryId) {
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
                option.optionId,
                option.value,
                optionVariation.value,
                productItem.quantity,
                productItem.price)
            .from(product)
            .join(product.category, category) // Assuming a direct association between Product and Category
            .join(product.productItems, productItem) // Assuming Product has a collection of ProductItems
            .join(productItem.productOptionVariations, productOptionVariation) // Assuming ProductItem has a collection of ProductOptionVariations
            .join(productOptionVariation.optionVariation, optionVariation) // Assuming ProductOptionVariation has an OptionVariation
            .join(optionVariation.option, option) // Assuming OptionVariation has an Option
            .where(category.categoryId.eq(categoryId))
            .orderBy(product.productId.asc())
            .fetch();
        
        List<com.cho.ecommerce.api.domain.ProductWithOptionsDTO> productWithOptionsDTOList = new ArrayList<>();
        for(Tuple tuple : results) {
            com.cho.ecommerce.api.domain.ProductWithOptionsDTO dto = new com.cho.ecommerce.api.domain.ProductWithOptionsDTO();
            dto.setProductId(tuple.get(product.productId));
            dto.setName(tuple.get(product.name));
            dto.setDescription(tuple.get(product.description));
            dto.setRating(tuple.get(product.rating));
            dto.setRatingCount(tuple.get(product.ratingCount));
            dto.setCategoryId(tuple.get(category.categoryId));
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
    public Optional<List<ProductEntity>> findProductDetailDTOsById(Long productId) {
        QProductEntity qProduct = QProductEntity.productEntity;
        QCategoryEntity qCategory = QCategoryEntity.categoryEntity;
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
        
        return Optional.ofNullable(Optional.ofNullable(
                products) //Optional can also be null, therefore wrap Optional with Optional (???!)
            .filter(not(List::isEmpty)) //if empty, returns Optional
            .orElseThrow(
                () -> new ResourceNotFoundException("Product not found, productId: "
                    + productId))); //throw Exception if result is Optional
    }
    
    @Override
    @Cacheable("topTenRatedProductsCached")
    public List<ProductEntity> findTop10ByRating() {
        QProductEntity product = QProductEntity.productEntity;
        
        return queryFactory
            .selectFrom(product)
            .orderBy(product.rating.desc())
            .limit(10)
            .fetch();
    }
}