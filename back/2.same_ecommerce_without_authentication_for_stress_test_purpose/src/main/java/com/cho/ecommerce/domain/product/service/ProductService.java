package com.cho.ecommerce.domain.product.service;

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
import com.cho.ecommerce.domain.product.repository.ProductOptionVariationRepository;
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
    
    public Page<ProductEntity> getProductsWithPagination(int page,
        int size) { //TODO - change name from PaginatedProductResponse to PaginatedProductResponseDTO
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
        Optional<ProductEntity> productEntitiesOptional = productRepository.findProductDetailDTOsById(
            productId);
        
        if (!productEntitiesOptional.isPresent()) {
            throw new ResourceNotFoundException("Product not found with ID: " + productId);
        }
        
        ProductEntity queryResult = productEntitiesOptional.get();
        
        List<ProductOptionVariationEntity> allProducts = new ArrayList<>();
        List<Product> productList = new ArrayList<>();
        
        //해당 product에 종속된 productItem을 뽑는다. 여기에 product_option_variations와 discounts가 걸려있다.
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
                .categoryName(categoryEntity.getName())
                .optionId(optionEntity.getOptionId())
                .optionName(optionEntity.getValue())
                .optionVariationId(optionVariationEntity.getOptionVariationId())
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
        ProductEntity productEntity = productRepository.findById(product.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
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
    
}
