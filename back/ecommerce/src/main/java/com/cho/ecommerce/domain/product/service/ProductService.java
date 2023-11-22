package com.cho.ecommerce.domain.product.service;

import com.cho.ecommerce.api.domain.PaginatedProductResponse;
import com.cho.ecommerce.api.domain.ProductCreateDTO;
import com.cho.ecommerce.api.domain.ProductDTO;
import com.cho.ecommerce.api.domain.ProductDetailDTO;
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
    
    public PaginatedProductResponse getProductsWithPagination(int page, int size) {
        Page<ProductEntity> productsWithPaginations = productRepository.findAll(
            PageRequest.of(page, size));
        return productMapper.buildPaginatedProductResponse(
            productsWithPaginations);
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
    
    public List<ProductDetailDTO> findProductDetailDTOsById(Long productId) {
        List<Product> productDetailDTOsList = self.getProductDetailDTOsById(
            productId); //fix: solution to "Methods should not call same-class methods with incompatible @Transactional"
        return productMapper.productsToProductDetailDTOs(productDetailDTOsList);
    }
    
    @Transactional
    public ProductDTO saveProduct(ProductCreateDTO product) {
        ProductEntity productEntity = productMapper.productCreateDTOToProductEntity(product);
        CategoryEntity category = categoryRepository.findByCategoryId(
            Long.valueOf(product.getCategoryId()));
        productEntity.setCategory(category);
        ProductEntity savedProduct = productRepository.save(productEntity);
        return productMapper.productEntityToProductDTO(savedProduct);
    }
    
    public ProductDTO saveProduct(ProductDTO product) {
        ProductEntity productEntity = productMapper.productDTOToProductEntity(product);
        CategoryEntity category = categoryRepository.findByCategoryId(
            Long.valueOf(product.getCategoryId()));
        productEntity.setCategory(category);
        ProductEntity savedProduct = productRepository.save(productEntity);
        return productMapper.productEntityToProductDTO(savedProduct);
    }
    
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    public ProductListResponseDTO findAllProductsByCategory(Long categoryId) {
        List<ProductEntity> allProductsByCategory = productRepositoryCustom.findAllProductsByCategory(
            categoryId);
        
        return productMapper.productEntitiesToProductListResponseDTOs(allProductsByCategory);
    }
}
