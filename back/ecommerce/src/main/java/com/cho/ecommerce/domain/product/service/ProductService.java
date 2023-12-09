package com.cho.ecommerce.domain.product.service;

import com.cho.ecommerce.api.domain.PaginatedProductResponse;
import com.cho.ecommerce.api.domain.ProductDTO;
import com.cho.ecommerce.api.domain.ProductListResponseDTO;
import com.cho.ecommerce.domain.product.domain.Product;
import com.cho.ecommerce.domain.product.entity.CategoryEntity;
import com.cho.ecommerce.domain.product.entity.DiscountEntity;
import com.cho.ecommerce.domain.product.entity.OptionEntity;
import com.cho.ecommerce.domain.product.entity.OptionVariationEntity;
import com.cho.ecommerce.domain.product.entity.ProductEntity;
import com.cho.ecommerce.domain.product.entity.ProductItemEntity;
import com.cho.ecommerce.domain.product.entity.ProductOptionVariationEntity;
import com.cho.ecommerce.domain.product.mapper.DiscountMapper;
import com.cho.ecommerce.domain.product.mapper.ProductMapper;
import com.cho.ecommerce.domain.product.repository.CategoryRepository;
import com.cho.ecommerce.domain.product.repository.ProductRepository;
import com.cho.ecommerce.domain.product.repository.ProductRepositoryCustomImpl;
import com.cho.ecommerce.global.error.exception.business.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    
    private ProductService self;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ProductRepositoryCustomImpl productRepositoryCustom;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private DiscountMapper discountMapper;
    
    @Autowired
    public void setSelf(ProductService self) {
        this.self = self;
    }
    
    public Page<ProductEntity> getProductsWithPagination(int page, int size) { //TODO - change name from PaginatedProductResponse to PaginatedProductResponseDTO
        return productRepository.findAll(PageRequest.of(page, size));
    }
    
    public List<ProductEntity> getTop10RatedProducts() {
        return productRepository.findTop10ByRating();
    }
    
    public Optional<ProductEntity> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    @Transactional
    public List<Product> getProductDetailDTOsById(Long productId) {
        Optional<List<ProductEntity>> productEntitiesOptional = productRepository.findProductDetailDTOsById(
            productId);
        
        if (!productEntitiesOptional.isPresent() || productEntitiesOptional.get().isEmpty()) {
            throw new ResourceNotFoundException("Product not found with ID: " + productId);
        }
        
        ProductEntity queryResult = productEntitiesOptional.get().get(0);
        
        List<ProductOptionVariationEntity> allProducts = new ArrayList<>();
        List<Product> productList = new ArrayList<>();
        
        if (queryResult.getProductItems() != null) {
            for (ProductItemEntity productItem : queryResult.getProductItems()) {
                for (ProductOptionVariationEntity productOptionVariation : productItem.getProductOptionVariations()) {
                    allProducts.add(productOptionVariation);
                }
            }
        }
        
        for (ProductOptionVariationEntity productOptionVariationEntity : allProducts) {
            ProductItemEntity productItemEntity = productOptionVariationEntity.getProductItem();
            ProductEntity productEntity = productItemEntity.getProduct();
            List<DiscountEntity> discounts = productItemEntity.getDiscounts();
            OptionVariationEntity optionVariationEntity = productOptionVariationEntity.getOptionVariation();
            OptionEntity optionEntity = optionVariationEntity.getOption();
            CategoryEntity categoryEntity = optionEntity.getCategory();
            
            Product product = new Product.Builder().productId(productEntity.getProductId())
                .name(productEntity.getName()).description(productEntity.getDescription())
                .rating(productEntity.getRating()).ratingCount(productEntity.getRatingCount())
                .quantity(productItemEntity.getQuantity()).price(productItemEntity.getPrice())
                .discounts(discountMapper.discountEntitiesToDiscounts(discounts))
                .categoryId(categoryEntity.getCategoryId())
                .categoryCode(categoryEntity.getCategoryCode())
                .categoryName(categoryEntity.getName()).optionName(optionEntity.getValue())
                .optionVariationName(optionVariationEntity.getValue()).build();
            productList.add(product);
        }
        
        return productList;
    }
    
    @Transactional
    public ProductEntity saveProduct(com.cho.ecommerce.api.domain.ProductCreateRequestDTO product) {
        ProductEntity productEntity = productMapper.productCreateDTOToProductEntity(product);
        CategoryEntity category = categoryRepository.findByCategoryId(
            Long.valueOf(product.getCategoryId()));
        productEntity.setCategory(category);
        return productRepository.save(productEntity);
    }
    
    @Transactional
    public ProductEntity updateProduct(com.cho.ecommerce.api.domain.ProductDTO product) {
        //1. read productEntity from database to check if it exist.
        ProductEntity productEntity = productRepository.findById(product.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        //2. set productEntity's columns with new productDTO's columns
        productEntity.setName(product.getName());
        productEntity.setDescription(product.getDescription());
        productEntity.setRating(product.getRating());
        productEntity.setRatingCount(product.getRatingCount());
        
        //3. save productEntity
        return productRepository.save(productEntity);
    }
    
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    public List<ProductEntity> findAllProductsByCategory(Long categoryId) {
        return productRepositoryCustom.findAllProductsByCategory(
            categoryId);
    }
}
