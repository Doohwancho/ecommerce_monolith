package com.cho.ecommerce.global.config.batch.step;

import org.springframework.context.annotation.Configuration;

@Configuration
public class DeprecatedInsertFakeProductStepConfig {
    
//    private final Logger log = LoggerFactory.getLogger(InsertFakeProductStepConfig.class);
//
//
//    private final Faker faker = new Faker();
//    @Autowired
//    private ProductRepository productRepository;
//    @Autowired
//    private CategoryRepository categoryRepository;
//    @Autowired
//    private OptionRepository optionRepository;
//    @Autowired
//    private OptionVariationRepository optionVariationRepository;
//
//    public String sizeTrimmer(String str, int size) {
//        int len = str.length();
//        if (len >= size) {
//            return str.substring(len - size, len - 1);
//        }
//        return str;
//    }
//
//    @Bean
//    @StepScope
//    public ItemReader<List<CategoryEntity>> generateCategoryAndOptionsReader(
//        @Value("#{jobParameters['numberOfFakeCategories']}") Long numberOfFakeCategories,
//        @Value("#{jobParameters['numberOfFakeOptionsPerCategory']}") Long numberOfFakeOptionsPerCategory,
//        @Value("#{jobParameters['numberOfFakeOptionVariationsPerOption']}") Long numberOfFakeOptionVariationsPerOption
//    ) {
//        return new ItemReader<List<CategoryEntity>>() {
//            boolean batchDataRead = false;
//
//            @Override
//            public List<CategoryEntity> read() throws Exception { //주의! @Override read()안에 코드 써야 chunk의 @Transaction이 올바르게 적용된다. 밖에 쓰면 다른 chunk의 transaction, 서순이 꼬일 수 있다.
//                List<CategoryEntity> categoryList = new ArrayList<>();
//
//                for(int i = 0; i < numberOfFakeCategories.intValue(); i++) { //TODO - error: 이상하게 카테고리만 x2로 넣어진다. (ex. 5개 넣으면 10개 들어감)
//                    CategoryEntity category = new CategoryEntity();
//                    String categoryCode = sizeTrimmer(faker.code().asin(),
//                        DatabaseConstants.CATEGORY_CODE_SIZE);
//                    String categoryName = sizeTrimmer(faker.commerce().department(),
//                        DatabaseConstants.CATEGORY_NAME_SIZE);
//
//                    category.setCategoryCode(categoryCode);
//                    category.setName(categoryName);
//
//                    // Generate options for each category
//                    Set<OptionEntity> options = new HashSet<>();
//                    for (int j = 0; j < numberOfFakeOptionsPerCategory.intValue(); j++) {
//                        OptionEntity option = new OptionEntity();
//                        String optionValue = sizeTrimmer(faker.commerce().material(),
//                            DatabaseConstants.OPTION_VALUE_SIZE);
//
//                        option.setValue(optionValue);
//                        option.setCategory(category);
//                        option.setOptionVariations(new ArrayList<>());
//                        optionRepository.save(option);
//                        options.add(option);
//
//                        // Generate option variations for each option
//                        for (int k = 0; k < numberOfFakeOptionVariationsPerOption.intValue(); k++) {
//                            OptionVariationEntity optionVariation = new OptionVariationEntity();
//
//                            String optionVariationValue = sizeTrimmer(faker.color().name(),
//                                DatabaseConstants.OPTION_VARIATION_VALUE_SIZE);
//
//                            optionVariation.setValue(optionVariationValue);
//                            optionVariation.setOption(option);
//
//                            optionVariationRepository.save(optionVariation);
//
//                            option.getOptionVariations().add(optionVariation);
//                        }
//                    }
//                    category.setOptionEntities(options);
//                    categoryList.add(category);
//                }
//
//                categoryRepository.saveAll(categoryList);
//
//                if (!batchDataRead) { //딱 한번만 보내고 이후부터는 null을 보내 reader()를 끝낸다.
//                    batchDataRead = true;
//                    return categoryList;
//                } else {
//                    return null; // Signal end of data
//                }
//            }
//        };
//    }
//
//    @Bean
//    @StepScope
//    public ItemProcessor<List<CategoryEntity>, List<ProductEntity>> generateFakeProductProcessor(
//        @Value("#{jobParameters['numberOfFakeProducts']}") Long numberOfFakeProducts,
//        @Value("#{jobParameters['numberOfFakeProductItemsPerProduct']}") Long numberOfFakeProductItemsPerProduct
//    ) {
//        return new ItemProcessor<List<CategoryEntity>, List<ProductEntity>>() {
//            @Override
//            public List<ProductEntity> process(List<CategoryEntity> categoryList) throws Exception {
//                List<ProductEntity> productList = new ArrayList<>();
//
//                for (int i = 0; i < numberOfFakeProducts.intValue(); i++) {
//                    //product 생성
//                    ProductEntity product = new ProductEntity();
//
//                    String productName = sizeTrimmer(faker.commerce().productName(),
//                        DatabaseConstants.PRODUCT_NAME_SIZE);
//                    String productDescription = sizeTrimmer(faker.lorem().sentence(),
//                        DatabaseConstants.PRODUCT_DESCRIPTION_SIZE);
//                    Double productRating = faker.number().randomDouble(1, 1, 5);
//                    Integer productRatingCount = faker.number().numberBetween(1, 1000);
//
//                    product.setName(productName);
//                    product.setDescription(productDescription);
//                    product.setRating(productRating);
//                    product.setRatingCount(productRatingCount);
//
//                    //category 적용
//                    CategoryEntity randomCategory = categoryList.get(
//                        faker.number().numberBetween(0, categoryList.size() - 1)); //random category
//                    product.setCategory(randomCategory);
//
//                    //productItem 적용
//                    Set<ProductItemEntity> productItems = new HashSet<>();
//                    product.setProductItems(productItems);
//
//                    for (int j = 0; j < numberOfFakeProductItemsPerProduct.intValue(); j++) {
//                        ProductItemEntity productItem = new ProductItemEntity();
//                        productItems.add(productItem);
//
//                        Integer productItemQuantity = faker.number().numberBetween(1, 100);
//                        Double productItemPrice = faker.number().randomDouble(2, 1, 10000);
//
//                        productItem.setQuantity(productItemQuantity);
//                        productItem.setPrice(productItemPrice);
//                        productItem.setProduct(product);
//
//                        //create product_option_variation
//                        ProductOptionVariationEntity productOptionVariationEntity = new ProductOptionVariationEntity();
//
//                        OptionVariationEntity optionVariation = product.getCategory()
//                            .getOptionEntities()
//                            .stream().findFirst().get().getOptionVariations().get(0);
//
//                        Set productOptionVariationSet = new HashSet();
//                        productOptionVariationSet.add(productOptionVariationEntity);
//                        optionVariation.setProductOptionVariations(productOptionVariationSet);
//                        productItem.setProductOptionVariations(productOptionVariationSet);
//
//                        productOptionVariationEntity.setOptionVariation(optionVariation);
//                        productOptionVariationEntity.setProductItem(productItem);
//
//
//                        //Generate discounts for each product item
//                        DiscountEntity discount = new DiscountEntity();
//                        discount.setDiscountType(
//                            DiscountType.values()[faker.number()
//                                .numberBetween(0, DiscountType.values().length)]);
//                        discount.setDiscountValue(faker.number().randomDouble(2, 1, 100));
////                discount.setStartDate(faker.date().past(10, TimeUnit.DAYS));
//                        discount.setStartDate(
//                            faker.date().past(10, TimeUnit.DAYS).toInstant()
//                                .atZone(ZoneId.systemDefault())
//                                .toOffsetDateTime());
////                discount.setEndDate(faker.date().future(10, TimeUnit.DAYS));
//                        discount.setEndDate(faker.date().future(10, TimeUnit.DAYS).toInstant()
//                            .atZone(ZoneId.systemDefault()).toOffsetDateTime());
//                        discount.setProductItem(productItem);
//
//                        List<DiscountEntity> discountList = new ArrayList<>();
//                        discountList.add(discount);
//                        productItem.setDiscounts(discountList);
//                    }
//                    productList.add(product);
//                }
//                return productList;
//            }
//        };
//    }
//
//    @Bean
//    public ItemWriter<List<ProductEntity>> InsertFakeProductWriter() {
//        return new ItemWriter<List<ProductEntity>>() {
//            @Override
//            public void write(List<? extends List<ProductEntity>> products) {
//                List<ProductEntity> flatList = products.stream()
//                    .flatMap(List::stream)
//                    .collect(Collectors.toList());
//
//                productRepository.saveAll(flatList);
//            }
//        };
//    }
//
//    @Bean
//    public Step generateFakeProductStep(StepBuilderFactory stepBuilderFactory,
////        PlatformTransactionManager transactionManager,
//        ItemReader<List<CategoryEntity>> queryCategoryAndOptionsReader,
//        ItemProcessor<List<CategoryEntity>, List<ProductEntity>> generateFakeProductProcessor,
//        ItemWriter<List<ProductEntity>> InsertFakeProductWriter) {
//
//        // note! - spring batch는 외부 transaction을 허용하지 않는다. Step에서 트랜젝션 만들어서 넣어줘야 한다.
////        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
////        attribute.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
////        attribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
////        attribute.setTimeout(30); // 30 seconds
//
//        return stepBuilderFactory.get("insertFakeProductStep")
//            .<List<CategoryEntity>, List<ProductEntity>>chunk(
//                1000) //<?, ?>에서 첫번째 인자는 .reader()가 리턴하는 인자이고, 두번째 인자는 writer()가 리턴하는 인자이다.
//            .reader(queryCategoryAndOptionsReader)
//            .processor(generateFakeProductProcessor)
//            .writer(InsertFakeProductWriter)
////            .transactionManager(transactionManager)
////            .transactionAttribute(attribute)
//            .build();
//    }
}
