package com.cho.ecommerce.global.config.batch.step;

import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.repository.AuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserAuthorityRepository;
import com.cho.ecommerce.domain.member.repository.UserRepository;
import com.cho.ecommerce.domain.order.entity.OrderEntity;
import com.cho.ecommerce.domain.order.entity.OrderItemEntity;
import com.cho.ecommerce.domain.order.repository.OrderRepository;
import com.cho.ecommerce.domain.product.entity.ProductOptionVariationEntity;
import com.cho.ecommerce.domain.product.repository.CategoryRepository;
import com.cho.ecommerce.domain.product.repository.OptionRepository;
import com.cho.ecommerce.domain.product.repository.OptionVariationRepository;
import com.cho.ecommerce.domain.product.repository.ProductOptionVariationRepository;
import com.cho.ecommerce.domain.product.repository.ProductRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class InsertFakeOrderStepConfig {
    private final Logger log = LoggerFactory.getLogger(InsertFakeOrderStepConfig.class);
    private final Faker faker = new Faker();
    
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private UserAuthorityRepository userAuthorityRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private OptionRepository optionRepository;
    @Autowired
    private OptionVariationRepository optionVariationRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductOptionVariationRepository productOptionVariationRepository;
    
    
    //query all users
    @Bean
    public ItemReader<List<UserEntity>> generateOrderReader() {
        return new ItemReader<List<UserEntity>>() {
            boolean batchDataRead = false;
            
            @Override
            public List<UserEntity> read() throws Exception { //주의! @Override read()안에 코드 써야 chunk의 @Transaction이 올바르게 적용된다. 밖에 쓰면 다른 chunk의 transaction, 서순이 꼬일 수 있다.
                List<UserEntity> userList = userRepository.findAll();
    
                if (!batchDataRead) { //딱 한번만 보내고 이후부터는 null을 보내 reader()를 끝낸다.
                    batchDataRead = true;
                    return userList;
                } else {
                    return null; // Signal end of data
                }
            }
        };
    }
    
    //make order and orderITems
    @Bean
    @StepScope
    public ItemProcessor<List<UserEntity>, List<OrderEntity>> generateFakeOrderProcessor(
        @Value("#{jobParameters['numberOfFakeOrderItemsPerOrder']}") Long numberOfFakeOrderItemsPerOrder
    ) {
        return new ItemProcessor<List<UserEntity>, List<OrderEntity>>() {
            @Override
            public List<OrderEntity> process(List<UserEntity> userList) throws Exception {
                List<ProductOptionVariationEntity> productOptionVariationList = productOptionVariationRepository.findAll();
                int productOptionVariationIndex = 0;
                int productOptionVariationSize = productOptionVariationList.size();
                
                List<OrderEntity> orderList = new ArrayList<>();
    
                //1유저당 1개의 order을 생성
                for(UserEntity user : userList) {
                    // Create a fake order
                    OrderEntity order = new OrderEntity();
                    orderList.add(order);
                    order.setOrderItems(new HashSet<>());
                    
                    // set member
                    order.setMember(user);
                    
                    // Convert Date to LocalDateTime
                    LocalDateTime orderDate = Instant.ofEpochMilli(
                            faker.date().past(730, TimeUnit.DAYS).getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                    order.setOrderDate(orderDate);
                    order.setOrderStatus("Confirmed");
                    
                    //order 한개당 user수 대비 3개의 orderItems를 저장한다
                    for (int i = 0; i < numberOfFakeOrderItemsPerOrder.intValue(); i++) {
                        OrderItemEntity orderItem = new OrderItemEntity();
                        order.getOrderItems().add(orderItem);
                        orderItem.setOrder(order);
        
                        ProductOptionVariationEntity productOptionVariation = productOptionVariationList.get(productOptionVariationIndex);
                        productOptionVariationIndex = (productOptionVariationIndex + 1) % productOptionVariationSize; //index 초과 안나도록 한다.
                        orderItem.setProductOptionVariation(productOptionVariation);
                    }
                }
                return orderList;
            }
        };
    }
    
    //save List<Order> will also save OrderItems (cascade)
    @Bean
    public ItemWriter<List<OrderEntity>> InsertFakeOrderWriter() {
        return new ItemWriter<List<OrderEntity>>() {
            @Override
            public void write(List<? extends List<OrderEntity>> orderList) {
                List<OrderEntity> flatList = orderList.stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

                orderRepository.saveAll(flatList);
            }
        };
    }
    
    @Bean
    public Step generateFakeOrderStep(StepBuilderFactory stepBuilderFactory,
//        PlatformTransactionManager transactionManager,
        ItemReader<List<UserEntity>> generateOrderReader,
        ItemProcessor<List<UserEntity>, List<OrderEntity>> generateFakeOrderProcessor,
        ItemWriter<List<OrderEntity>> InsertFakeOrderWriter) {
        
        // note! - spring batch는 외부 transaction을 허용하지 않는다. Step에서 트랜젝션 만들어서 넣어줘야 한다.
//        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
//        attribute.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
//        attribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
//        attribute.setTimeout(30); // 30 seconds
        
        return stepBuilderFactory.get("insertFakeOrderStep")
            .<List<UserEntity>, List<OrderEntity>>chunk(
                1000) //<?, ?>에서 첫번째 인자는 .reader()가 리턴하는 인자이고, 두번째 인자는 writer()가 리턴하는 인자이다.
            .reader(generateOrderReader)
            .processor(generateFakeOrderProcessor)
            .writer(InsertFakeOrderWriter)
//            .transactionManager(transactionManager)
//            .transactionAttribute(attribute)
            .build();
    }
}
