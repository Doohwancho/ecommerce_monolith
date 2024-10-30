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
import com.cho.ecommerce.domain.product.repository.ProductRepository;
import com.cho.ecommerce.domain.product.repository.ProductRepositoryCustomImpl;
import com.cho.ecommerce.global.error.exception.business.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    
    private final Logger log = LoggerFactory.getLogger(ProductService.class);
    
    private static final String PRODUCT_CACHE_KEY = "product:detail:";
    private static final long CACHE_TTL = 3600; // 1시간
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
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
        // 1. Look aside: Try to get from cache first
        String cacheKey = PRODUCT_CACHE_KEY + productId;
        List<Product> cachedProducts = getCachedProducts(cacheKey);
        
        if (cachedProducts != null) {
            return cachedProducts;
        }
        
        // 2. Cache miss: Get from DB
        Optional<ProductEntity> productEntitiesOptional = productRepository.findProductDetailDTOsById(
            productId);
        
        if (!productEntitiesOptional.isPresent()) {
            throw new ResourceNotFoundException("Product not found with ID: " + productId);
        }
        
        // 3. Transform data
        List<Product> products = transformToProducts(productEntitiesOptional.get());
        
        // 4. Write to cache
        cacheProducts(cacheKey, products);
        
        return products;
    }
    
    private List<Product> getCachedProducts(String key) {
        try {
            return (List<Product>) redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis cache get error", e);
            return null;
        }
    }
    
    private void cacheProducts(String key, List<Product> products) {
        try {
            redisTemplate.opsForValue().set(key, products, CACHE_TTL, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Redis cache set error", e);
        }
    }
    
    private List<Product> transformToProducts(ProductEntity queryResult) {
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
    
    // Write Through 구현: 상품 수정 시 캐시도 함께 업데이트
    @Transactional
    public ProductEntity updateProduct(com.cho.ecommerce.api.domain.ProductDTO product) {
        ProductEntity updatedProduct = productRepository.findById(product.getProductId())
            .map(productEntity -> {
                productEntity.setName(product.getName());
                productEntity.setDescription(product.getDescription());
                productEntity.setRating(product.getRating());
                productEntity.setRatingCount(product.getRatingCount());
                return productRepository.save(productEntity);
            })
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        // Update cache
        String cacheKey = PRODUCT_CACHE_KEY + product.getProductId();
        redisTemplate.delete(cacheKey);  // Invalidate cache for this product
        
        return updatedProduct;
    }
    
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
}
