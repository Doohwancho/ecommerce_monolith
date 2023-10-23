package com.cho.ecommerce.domain.product.mapper;

import com.cho.ecommerce.api.domain.ProductDTO;
import com.cho.ecommerce.domain.product.entity.ProductEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.springframework.web.bind.annotation.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    ProductDTO productEntityToProductDTO(ProductEntity productEntity);
    
    ProductEntity productDTOToProductEntity(ProductDTO productDTO);
    
    List<ProductDTO> productEntitiesToProductDTOs(List<ProductEntity> productEntities);
    List<ProductEntity> productDTOsToProductEntities(List<ProductDTO> productDTOs);
}