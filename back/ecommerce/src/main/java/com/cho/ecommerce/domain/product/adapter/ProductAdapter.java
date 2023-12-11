package com.cho.ecommerce.domain.product.adapter;

import com.cho.ecommerce.api.domain.ProductCreateRequestDTO;
import com.cho.ecommerce.api.domain.ProductDTO;
import com.cho.ecommerce.domain.product.domain.Product;
import com.cho.ecommerce.domain.product.entity.CategoryEntity;
import com.cho.ecommerce.domain.product.entity.ProductEntity;
import com.cho.ecommerce.domain.product.mapper.ProductMapper;
import com.cho.ecommerce.domain.product.service.ProductService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ProductAdapter {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductService productService;
    
    
    public com.cho.ecommerce.api.domain.PaginatedProductResponse getProductsWithPagination(
        Integer page, Integer size) {
        Page<ProductEntity> productsWithPagination = productService.getProductsWithPagination(page, size);
        return productMapper.buildPaginatedProductResponse(productsWithPagination);
    }
    
    public List<com.cho.ecommerce.api.domain.ProductDetailResponseDTO> getProductDetailDTOsById(
        Long id) {
        List<Product> productList = productService.getProductDetailDTOsById(id);
        return productMapper.productsToProductDetailDTOs(productList);
    }
    
    public com.cho.ecommerce.api.domain.ProductDTO saveProduct(
        ProductCreateRequestDTO product) {
        ProductEntity productEntity = productService.saveProduct(product);
        return productMapper.productEntityToProductDTO(productEntity);
    }
    
    public com.cho.ecommerce.api.domain.ProductDTO updateProduct(ProductDTO product) {
        ProductEntity productEntity = productService.updateProduct(product);
        return productMapper.productEntityToProductDTO(productEntity);
    }
    
    public List<com.cho.ecommerce.api.domain.ProductDTO> getTop10RatedProducts() {
        List<ProductEntity> top10ProductsByRating = productService.getTop10RatedProducts();
        return productMapper.productEntitiesToProductDTOs(top10ProductsByRating);
    }
}
