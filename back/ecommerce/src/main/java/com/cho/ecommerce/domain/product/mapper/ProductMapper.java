package com.cho.ecommerce.domain.product.mapper;

import com.cho.ecommerce.api.domain.ProductCreateDTO;
import com.cho.ecommerce.api.domain.ProductDTO;
import com.cho.ecommerce.api.domain.ProductListResponse;
import com.cho.ecommerce.domain.product.entity.CategoryEntity;
import com.cho.ecommerce.domain.product.entity.ProductEntity;
import com.cho.ecommerce.domain.product.service.CategoryService;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    //fixed - error: No property named "PRODUCT_ID" exists in source parameter(s). Did you mean "PRODUCTID"?
    //mapper가 getter부를 때, openapi가 만든 dto안에 getter 이름의 format과 달라서 @Mapping()이 작동하지 않는 현상.
    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "rating", target = "rating")
    @Mapping(source = "ratingCount", target = "ratingCount")
    @Mapping(source = "category.categoryId", target = "categoryId")
    ProductDTO productEntityToProductDTO(ProductEntity productEntity);
    
    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "rating", target = "rating")
    @Mapping(source = "ratingCount", target = "ratingCount")
    @Mapping(target = "category", ignore = true) // Assuming you don't want to map the category back in this direction
    @Mapping(target = "productItems", ignore = true) // Assuming you don't want to map the productItems back in this direction
    ProductEntity productDTOToProductEntity(ProductDTO productDTO);
    
    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "rating", target = "rating")
    @Mapping(source = "ratingCount", target = "ratingCount")
    @Mapping(source = "category.categoryId", target = "categoryId")
    List<ProductDTO> productEntitiesToProductDTOs(List<ProductEntity> productEntities);
    
    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "rating", target = "rating")
    @Mapping(source = "ratingCount", target = "ratingCount")
    @Mapping(target = "category", ignore = true) // Assuming you don't want to map the category back in this direction
    List<ProductEntity> productDTOsToProductEntities(List<ProductDTO> productDTOs);
    
    
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "rating", ignore = true) // Assuming default or calculated separately
    @Mapping(target = "ratingCount", ignore = true) // Assuming default or calculated separately
    @Mapping(target = "productItems", ignore = true) // Assuming not needed for creation DTO
    @Mapping(target = "category", ignore = true) // Assuming you don't want to map the category back in this direction
    ProductEntity productCreateDTOToProductEntity(ProductCreateDTO productCreateDTO);
    

    default ProductListResponse productEntitiesToProductListResponses(List<ProductEntity> productEntityList) {
        ProductListResponse productListResponse = new ProductListResponse();
        productListResponse.setProducts(productEntitiesToProductDTOs(productEntityList));
        return productListResponse;
    }
}