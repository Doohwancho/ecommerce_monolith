package com.cho.ecommerce.domain.product.mapper;

import com.cho.ecommerce.api.domain.ProductCreateDTO;
import com.cho.ecommerce.api.domain.ProductDTO;
import com.cho.ecommerce.domain.product.entity.ProductEntity;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    ProductDTO productEntityToProductDTO(ProductEntity productEntity);
    ProductEntity productDTOToProductEntity(ProductDTO productDTO);
    
    List<ProductDTO> productEntitiesToProductDTOs(List<ProductEntity> productEntities);
    List<ProductEntity> productDTOsToProductEntities(List<ProductDTO> productDTOs);
    
    
    
    ProductEntity productCreateDTOToProductEntity(ProductCreateDTO productCreateDTO);
    List<ProductEntity> productCreateDTOsToProductEntities(List<ProductCreateDTO> productCreateDTOs);
}