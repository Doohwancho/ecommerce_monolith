package com.cho.ecommerce.Integration_test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.cho.ecommerce.Application;
import com.cho.ecommerce.api.domain.UserDetailsDTO;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.repository.UserRepository;
import com.cho.ecommerce.global.config.fakedata.FakeDataGenerator;
import com.cho.ecommerce.global.config.parser.OffsetDateTimeDeserializer;
import com.cho.ecommerce.global.util.DatabaseCleanup;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.OffsetDateTime;
import javax.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
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
class MemberIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;
    
    @Autowired
    private DatabaseCleanup databaseCleanup;
    @Autowired
    private FakeDataGenerator dataGenerator;
    
    @Autowired
    private UserRepository userRepository;
    
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
    
    private HttpEntity<MultiValueMap<String, String>> createHeaders(String username,
        String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", username);
        map.add("password", password);
        
        return new HttpEntity<>(map, headers);
    }
    
    @Transactional
    @Test
    void testGetUserDetails() {
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
        
        //get User from database in order to get userID
        UserEntity targetUser = userRepository.getById(3L);
        
        //when
        //http request to /user with session included.
        ResponseEntity<String> response = restTemplate.exchange(
            "http://localhost:" + port + "/users/" + targetUser.getUsername(),
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
        
        UserDetailsDTO userDetails = gson.fromJson(jsonInput, UserDetailsDTO.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(userDetails.getUsername());
        assertNotNull(userDetails.getEmail());
        assertNotNull(userDetails.getName());
        assertNotNull(userDetails.getAddress().getCity());
        assertNotNull(userDetails.getAddress().getCountry());
        assertNotNull(userDetails.getAddress().getState());
        assertNotNull(userDetails.getAddress().getStreet());
        assertNotNull(userDetails.getAddress().getZipCode());
        assertNotNull(userDetails.getRole());
        assertNotNull(userDetails.getEnabled());
        assertNotNull(userDetails.getCreated());
        assertNotNull(userDetails.getUpdated());
        assertNotNull(userDetails.getAuthorities());
    }
    
}
