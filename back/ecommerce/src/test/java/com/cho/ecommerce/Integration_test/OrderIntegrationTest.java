package com.cho.ecommerce.Integration_test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cho.ecommerce.Application;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.repository.UserRepository;
import com.cho.ecommerce.api.domain.OrderItemDetailsDTO;
import com.cho.ecommerce.domain.product.domain.Product;
import com.cho.ecommerce.global.config.fakedata.FakeDataGenerator;
import com.cho.ecommerce.global.config.parser.OffsetDateTimeDeserializer;
import com.cho.ecommerce.global.util.DatabaseCleanup;
import com.cho.ecommerce.smoke_test.MemberSmokeTest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {Application.class})
@ActiveProfiles("test")
@Tag("integration") //to run, type "mvn test -Dgroups=integration"
public class OrderIntegrationTest {
    private final Logger log = LoggerFactory.getLogger(MemberSmokeTest.class);
    
    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DatabaseCleanup databaseCleanup;
    @Autowired
    private FakeDataGenerator dataGenerator;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    
    @BeforeEach
    @AfterEach
    public void clearSecurityContext() {
        //1. clear security context and redis that stored user-session
        //session을 지우지 않으면, SecurityConfig -> .maxSessionsPreventsLogin(true) 때문에, 이후에 로그인해서 생성되는 session이 제한되어 http status 302 REDIRECT: /login?error 로 이동하기 때문
        SecurityContextHolder.clearContext();
        flushAllRedisData();
        
        //2. delete all data in database
//        databaseCleanup.afterPropertiesSet();
//        databaseCleanup.execute();
    }
    
    public void flushAllRedisData() {
        // clear all data from Redis
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        
        // Verify that Redis is empty (optional)
        Long size = redisTemplate.getConnectionFactory().getConnection().dbSize();
        assert size == 0 : "Redis database is not empty!";
    }
    
    private HttpEntity<MultiValueMap<String, String>> createHeaders(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", username);
        map.add("password", password);
        
        return new HttpEntity<>(map, headers);
    }
    
    @Test
    public void GetOrderItemsDetailDTOsByUsername() {
        //given
        ResponseEntity<String> responseWithSession = restTemplate.postForEntity(
            "http://localhost:" + port + "/login",
            createHeaders("testUser", "password"),
            String.class
        );
        
        //Extract Set-Cookie header (session cookie)
        String setCookieHeader = responseWithSession.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertNotNull(setCookieHeader, "Set-Cookie header should not be null");
        
        //Create new headers and set the Cookie header with the session cookie
        HttpHeaders headersWithSessionCookie = new HttpHeaders();
        headersWithSessionCookie.add(HttpHeaders.COOKIE, setCookieHeader);
        
        //get username from database
        UserEntity user = userRepository.findById(3L).get();
        String userName = user.getUsername();
        
        //when
        //http request to /user with session included.
        ResponseEntity<String> response = restTemplate.exchange(
            "http://localhost:" + port + "/orders/orderItems/" + userName,
            HttpMethod.GET,
            new HttpEntity<>(headersWithSessionCookie),
            String.class
        );
        
        //then
        //parse json http response into Product.java
        String jsonInput = response.getBody();
        
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeDeserializer())
            .create();
    
        OrderItemDetailsDTO[] orderItems = gson.fromJson(jsonInput, OrderItemDetailsDTO[].class);
        
        for(OrderItemDetailsDTO orderItem : orderItems) {
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(orderItem.getOrderItemId());
            assertNotNull(orderItem.getOrderId());
            assertNotNull(orderItem.getOrderDate());
            assertNotNull(orderItem.getOrderStatus());
            assertNotNull(orderItem.getMemberId());
            assertNotNull(orderItem.getUsername());
            assertNotNull(orderItem.getEmail());
            assertNotNull(orderItem.getName());
            assertNotNull(orderItem.getRole());
            assertNotNull(orderItem.getCreatedAt());
            assertNotNull(orderItem.getUpdatedAt());
            assertNotNull(orderItem.getProductId());
            assertNotNull(orderItem.getProductName());
            assertNotNull(orderItem.getDescription());
            assertNotNull(orderItem.getRating());
            assertNotNull(orderItem.getRatingCount());
            assertNotNull(orderItem.getOptionValue());
            assertNotNull(orderItem.getOptionVariationValue());
            assertNotNull(orderItem.getQuantity());
            assertNotNull(orderItem.getPrice());
        }
    }
}
