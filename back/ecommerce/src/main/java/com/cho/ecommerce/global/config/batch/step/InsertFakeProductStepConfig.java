package com.cho.ecommerce.global.config.batch.step;


import com.cho.ecommerce.domain.product.domain.DiscountType;
import com.cho.ecommerce.domain.product.entity.CategoryEntity;
import com.cho.ecommerce.domain.product.entity.DiscountEntity;
import com.cho.ecommerce.domain.product.entity.OptionEntity;
import com.cho.ecommerce.domain.product.entity.OptionVariationEntity;
import com.cho.ecommerce.domain.product.entity.ProductEntity;
import com.cho.ecommerce.domain.product.entity.ProductItemEntity;
import com.cho.ecommerce.domain.product.entity.ProductOptionVariationEntity;
import com.cho.ecommerce.domain.product.repository.CategoryRepository;
import com.cho.ecommerce.domain.product.repository.OptionVariationRepository;
import com.cho.ecommerce.domain.product.repository.ProductRepository;
import com.cho.ecommerce.global.config.database.DatabaseConstants;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import net.datafaker.Faker;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InsertFakeProductStepConfig {
    
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private OptionVariationRepository optionVariationRepository;
    
    private final Faker faker = new Faker();
    
    public String sizeTrimmer(String str, int size){
        int len = str.length();
        if(len >= size) {
            return str.substring(len-size, len-1);
        }
        return str;
    }
    
    public ProductEntity generateRandomProductEntity(int numberOfFakeProductItemsPerProduct) {
        //product 생성
        ProductEntity product = new ProductEntity();
    
        String productName = sizeTrimmer(faker.commerce().productName(),
            DatabaseConstants.PRODUCT_NAME_SIZE);
        String productDescription = sizeTrimmer(faker.lorem().sentence(),
            DatabaseConstants.PRODUCT_DESCRIPTION_SIZE);
        Double productRating = faker.number().randomDouble(1, 1, 5);
        Integer productRatingCount = faker.number().numberBetween(1, 1000);
    
        product.setName(productName);
        product.setDescription(productDescription);
        product.setRating(productRating);
        product.setRatingCount(productRatingCount);
    
        //productItem 적용
        Set<ProductItemEntity> productItems = new HashSet<>();
        product.setProductItems(productItems);
    
        for (int j = 0; j < numberOfFakeProductItemsPerProduct; j++) {
            ProductItemEntity productItem = new ProductItemEntity();
            productItems.add(productItem);
        
            Integer productItemQuantity = faker.number().numberBetween(1, 100);
            Double productItemPrice = faker.number().randomDouble(1, 1000, 210000);
        
            productItem.setQuantity(productItemQuantity);
            productItem.setPrice(productItemPrice);
            productItem.setProduct(product);
       
            //Generate discounts for each product item
            DiscountEntity discount = new DiscountEntity();
            discount.setDiscountType(
                DiscountType.values()[faker.number()
                    .numberBetween(0, DiscountType.values().length)]);
            discount.setDiscountValue(faker.number().randomDouble(2, 1, 100));
//                discount.setStartDate(faker.date().past(10, TimeUnit.DAYS));
            discount.setStartDate(
                faker.date().past(10, TimeUnit.DAYS).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toOffsetDateTime());
//                discount.setEndDate(faker.date().future(10, TimeUnit.DAYS));
            discount.setEndDate(faker.date().future(10, TimeUnit.DAYS).toInstant()
                .atZone(ZoneId.systemDefault()).toOffsetDateTime());
            discount.setProductItem(productItem);
        
            List<DiscountEntity> discountList = new ArrayList<>();
            discountList.add(discount);
            productItem.setDiscounts(discountList);
        }
    
        return product;
    }
    
    @Bean
    @StepScope
    public ItemReader<ProductEntity> generateFakeProductReader(
        @Value("#{jobParameters['numberOfFakeProducts']}") Long numberOfFakeProducts,
        @Value("#{jobParameters['numberOfFakeProductItemsPerProduct']}") Long numberOfFakeProductItemsPerProduct) {
    
        return new ItemReader<ProductEntity>() {
            final int NUMBER_OF_FAKE_PRODUCTS = numberOfFakeProducts.intValue();
            private int productCount = 0;
    
            @Override
            public ProductEntity read() {
                if (productCount < NUMBER_OF_FAKE_PRODUCTS) {
                    productCount++;
                    return generateRandomProductEntity(numberOfFakeProductItemsPerProduct.intValue());
                } else {
                    return null; // Return null to indicate the end of data
                }
            }
        };
    }
    
    @Bean
    @StepScope
    public ItemProcessor<ProductEntity, ProductEntity> addFakeCategoryAndProductOptionVariationProcessor() {
        return new ItemProcessor<ProductEntity, ProductEntity>() {
            List<CategoryEntity> categoryList = null;
            
            @Override
            public ProductEntity process(ProductEntity product) throws Exception {
                if(categoryList == null) {
                    categoryList = categoryRepository.findCategoriesByDepth(2);
                }
                
                //category 적용
                CategoryEntity randomCategory = categoryList.get(
                    faker.number().numberBetween(0, categoryList.size() - 1)); //random category (depth 2)
                product.setCategory(randomCategory);
    
                //create product_option_variation for each product_items
                for(ProductItemEntity productItem : product.getProductItems()) {
                    //1. extract random option that belongs to the product's category
                    Set<OptionEntity> optionEntitiesSet = product.getCategory().getOptionEntities();
                    int optionEntitySize = optionEntitiesSet.size();
                    Random rand = new Random();
                    OptionEntity randomOptionEntity = optionEntitiesSet.stream()
                        .skip(rand.nextInt(optionEntitySize))
                        .findFirst()
                        .orElse(null);
                    
                    //2. extract random option_variation from random option extracted
                    List<OptionVariationEntity> optionVariationsList = randomOptionEntity.getOptionVariations();
                    int optionVariationsListSize = optionVariationsList.size();
                    OptionVariationEntity optionVariationEntity = randomOptionEntity.getOptionVariations()
                        .stream()
                        .skip(rand.nextInt(optionVariationsListSize))
                        .findFirst()
                        .orElse(null);
    
                    //3. create product option variation Entity
                    ProductOptionVariationEntity productOptionVariationEntity = new ProductOptionVariationEntity();
                    
                    //4. option variation에서 product option variation을 저장
                    Set productOptionVariationSet = new HashSet();
                    productOptionVariationSet.add(productOptionVariationEntity);
                    optionVariationEntity.setProductOptionVariations(productOptionVariationSet);
    
                    //5. productItem에서 product option variation 저장
                    productItem.setProductOptionVariations(productOptionVariationSet);
    
                    //6. product option variation에서 productItem, option variation 저장
                    productOptionVariationEntity.setOptionVariation(optionVariationEntity);
                    productOptionVariationEntity.setProductItem(productItem);
                }
                
                return product;
            }
        };
    }
    
    @Bean
    public ItemWriter<ProductEntity> generateFakeProductWriter(EntityManager entityManager) {
        return new ItemWriter<ProductEntity>() {
            @Override
            public void write(List<? extends ProductEntity> products) {
                productRepository.saveAll(products);
            }
        };
    }
    
    @Bean
    public Step generateFakeProductStep(StepBuilderFactory stepBuilderFactory,
        ItemReader<ProductEntity> generateFakeProductReader,
        ItemProcessor<ProductEntity, ProductEntity> addFakeCategoryAndProductOptionVariationProcessor,
        ItemWriter<ProductEntity> generateFakeProductWriter) {
        
        return stepBuilderFactory.get("insertFakeProductStep")
            .<ProductEntity, ProductEntity>chunk(1000) //<UserEntity, UserEntity>에서 첫번째 인자는 .reader()가 리턴하는 인자이고, 두번째 인자는 writer()가 리턴하는 인자이다.
            .reader(generateFakeProductReader) //Spring Batch manages transactions at the chunk level
            .processor(addFakeCategoryAndProductOptionVariationProcessor)
            .writer(generateFakeProductWriter)
            .build();
    }
}
