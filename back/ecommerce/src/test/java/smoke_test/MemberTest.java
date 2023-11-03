package smoke_test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cho.ecommerce.Application;
import com.cho.ecommerce.domain.member.repository.UserRepository;
import com.cho.ecommerce.domain.member.service.AuthorityService;
import com.cho.ecommerce.domain.member.service.UserAuthorityService;
import com.cho.ecommerce.domain.member.service.UserService;
import com.cho.ecommerce.global.config.fakedata.FakeDataGenerator;
import com.cho.ecommerce.global.config.redis.RedisConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
@ContextConfiguration(classes = {Application.class, RedisConfig.class})
@ActiveProfiles("test")
@Tag("smoke") //to run, type "mvn test -Dgroups=smoke"
public class MemberTest {
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private UserAuthorityService userAuthorityService;
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FakeDataGenerator dataGenerator;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @LocalServerPort
    private int port;
    
    private final Logger log = LoggerFactory.getLogger(MemberTest.class);
    
    @BeforeEach
    public void clearSecurityContextBeforeTest() {
        SecurityContextHolder.clearContext();
        flushAllRedisData();
    }
    
    @AfterEach
    public void clearSecurityContextAfterTest() {
        SecurityContextHolder.clearContext();
        flushAllRedisData();
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
    @DisplayName("failed login attempt redirects user to /login page with HTTP status 302")
    public void whenLoginWithInvalidUserThenUnauthenticated() {
        ResponseEntity<String> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/login",
            new HttpEntity<>(createHeaders("invalidUser-asdfasdfasdf", "invalidPassword-asdfasdfasdf")),
            String.class
        );
        
        //step1) check http status is 302 when failed login attempt
        assertEquals(HttpStatus.FOUND, response.getStatusCode()); //http status 302
        
        //step2) redirect to /login page
        assertTrue(response.getHeaders().containsKey("Location"));
        String location = response.getHeaders().getFirst("Location");
        assertNotNull(location);
        assertTrue(location.endsWith("/login?error"));
    }
    @Test
    @DisplayName("login success with 'admin' returns 302 redirect and redirect to '/admin'")
    public void whenLoginWithValidUserThenAuthenticated() {
        ResponseEntity<String> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/login",
            createHeaders("admin", "admin"),
            String.class
        );
        
        //step1) login success, http status 302 redirect
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
    
        //step2) redirect to / page
        assertTrue(response.getHeaders().containsKey("Location"));
        String location = response.getHeaders().getFirst("Location");
        assertNotNull(location);
        assertTrue(location.endsWith("/admin"));
    }
    
    @Test
    @DisplayName("login attempt from the same user while his session is alive will redict him to /login page")
    public void attemptToLoginAgainWhileSessionIsAliveShouldFail() {
        whenLoginWithValidUserThenAuthenticated();
    
        ResponseEntity<String> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/login",
            createHeaders("admin", "admin"),
            String.class
        );
    
        //step1) login success, http status 302 redirect
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
    
        //step2) redirect to / page
        assertTrue(response.getHeaders().containsKey("Location"));
        String location = response.getHeaders().getFirst("Location");
        assertNotNull(location);
        assertTrue(location.endsWith("/login?error"));
    }

    
}


