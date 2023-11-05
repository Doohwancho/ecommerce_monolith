package com.cho.ecommerce.domain.product.mapper;

import com.cho.ecommerce.api.domain.ProductCreateDTO;
import com.cho.ecommerce.api.domain.ProductDTO;
import com.cho.ecommerce.api.domain.ProductListResponse;
import com.cho.ecommerce.domain.product.entity.ProductEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);
    
    //TODO - error - error: No property named "PRODUCT_ID" exists in source parameter(s). Did you mean "PRODUCTID"?

    ProductDTO productEntityToProductDTO(ProductEntity productEntity);
    
    ProductEntity productDTOToProductEntity(ProductDTO productDTO);
    
    @Mapping(source = "productId", target = "PRODUCT_ID")
    @Mapping(source = "name", target = "NAME")
    @Mapping(source = "description", target = "DESCRIPTION")
    @Mapping(source = "rating", target = "RATING")
    @Mapping(source = "ratingCount", target = "RATING_COUNT")
    @Mapping(source = "category.id", target = "CATEGORY_ID")
    List<ProductDTO> productEntitiesToProductDTOs(List<ProductEntity> productEntities);
    
    @Mapping(source = "PRODUCT_ID", target = "productId")
    @Mapping(source = "NAME", target = "name")
    @Mapping(source = "DESCRIPTION", target = "description")
    @Mapping(source = "RATING", target = "rating")
    @Mapping(source = "RATING_COUNT", target = "ratingCount")
    @Mapping(target = "category", ignore = true) // Assuming you don't want to map the category back in this direction
    List<ProductEntity> productDTOsToProductEntities(List<ProductDTO> productDTOs);
    
    
    
    ProductEntity productCreateDTOToProductEntity(ProductCreateDTO productCreateDTO);
    List<ProductEntity> productCreateDTOsToProductEntities(List<ProductCreateDTO> productCreateDTOs);
    
    List<ProductListResponse> productEntitiesToProductListResponses(List<ProductEntity> productEntityList);
}