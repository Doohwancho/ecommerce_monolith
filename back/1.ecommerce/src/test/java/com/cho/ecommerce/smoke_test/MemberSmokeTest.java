package com.cho.ecommerce.smoke_test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cho.ecommerce.Application;
import com.cho.ecommerce.api.domain.RegisterResponseDTO;
import com.cho.ecommerce.domain.member.entity.UserEntity;
import com.cho.ecommerce.domain.member.repository.UserRepository;
import com.cho.ecommerce.domain.member.service.AuthorityService;
import com.cho.ecommerce.domain.member.service.UserAuthorityService;
import com.cho.ecommerce.domain.member.service.UserService;
import com.cho.ecommerce.global.config.bulk_insert.fakedata.step1_jpa_saveAll.JpaFakeDataGenerator;
import com.cho.ecommerce.global.config.redis.RedisConfig;
import com.cho.ecommerce.global.config.security.SecurityConstants;
import java.util.List;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.session.Session;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {Application.class, RedisConfig.class})
@ActiveProfiles("local")
@Tag("smoke") //to run, type "mvn test -Dgroups=smoke"
class MemberSmokeTest<S extends Session> {
    
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private UserAuthorityService userAuthorityService;
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JpaFakeDataGenerator dataGenerator;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private SessionRegistry sessionRegistry;
    @Autowired
    private EntityManager entityManager;
    
    @LocalServerPort
    private int port;
    
    
    @BeforeEach
    @AfterEach
    public void clearSecurityContext() {
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
    
    private HttpEntity<MultiValueMap<String, String>> createHeaders(String username,
        String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", username);
        map.add("password", password);
        
        return new HttpEntity<>(map, headers);
    }
    
    @Test
    @DisplayName("failed login attempt redirects user to /login page with HTTP status 302")
    void whenLoginWithInvalidUserThenUnauthenticated() {
        ResponseEntity<String> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/login",
            new HttpEntity<>(
                createHeaders("invalidUser-asdfasdfasdf", "invalidPassword-asdfasdfasdf")),
            String.class
        );
        
        //step1) check http status is 302 when failed login attempt
        assertEquals(HttpStatus.FOUND, response.getStatusCode()); //http status 302
        
        //step2) redirect to /login page
        assertTrue(response.getHeaders().containsKey("Location"));
        String location = response.getHeaders().getFirst("Location");
        assertNotNull(location);
        assertTrue(location.endsWith("/login"));
    }
    
    @Test
    @DisplayName("login success with 'admin' returns 302 redirect and redirect to '/admin'")
    void whenLoginWithValidUserThenAuthenticated() {
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
    
    //SecurityConfig.java에 .maxSessionsPreventsLogin(true)를 테스트 한다.
    @Test
    @DisplayName("login attempt from the same user while his session is alive will redirect him to /login page")
    void attemptToLoginAgainWhileSessionIsAliveShouldFail() {
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
        assertTrue(location.endsWith("/login"));
    }
    
    @Test
    void 서버에서는_없는_session인데_client에서_http_request_with_that_session시_login_페이지로_redirect() {
        //step1) get session cookie after user login
        ResponseEntity<String> responseWithSession = restTemplate.postForEntity(
            "http://localhost:" + port + "/login",
            createHeaders("admin", "admin"),
            String.class
        );
        
        //Extract Set-Cookie header (session cookie)
        String setCookieHeader = responseWithSession.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertNotNull(setCookieHeader, "Set-Cookie header should not be null");
        
        //Create new headers and set the Cookie header with the session cookie
        HttpHeaders headersWithSessionCookie = new HttpHeaders();
        headersWithSessionCookie.add(HttpHeaders.COOKIE, setCookieHeader);
        
        //step2) clear spring security context and redis that stored sessions
        clearSecurityContext();
        
        //step3) http request to /user with session included.
        ResponseEntity<String> response = restTemplate.exchange(
            "http://localhost:" + port + "/user",
            HttpMethod.GET,
            new HttpEntity<>(headersWithSessionCookie),
            String.class
        );
        
        assertEquals(HttpStatus.OK,
            response.getStatusCode()); //302 redirect 이후 200, login page로 간다.
        assertNotNull(response.getBody());
    }
    
    @Test
    void whenUserLogsOutThenRedirectedToLoginPage() {
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
            "http://localhost:" + port + "/login",
            createHeaders("admin", "admin"),
            String.class
        );
        
        String setCookieHeader = loginResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, setCookieHeader);
        
        // Now, we perform the logout
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> logoutResponse = restTemplate.exchange(
            "http://localhost:" + port + "/logout",
            HttpMethod.POST,
            requestEntity,
            String.class
        );
        
        // Check that we are redirected to the login page with the 'logout' parameter
        assertEquals(HttpStatus.FOUND, logoutResponse.getStatusCode());
        String location = logoutResponse.getHeaders().getLocation().toString();
        assertEquals("http://localhost:" + port + "/login?logout", location);
        
        // Optionally, check that the JSESSIONID cookie has been invalidated
        List<String> cookies = logoutResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
        boolean cookieInvalidated = cookies.stream().anyMatch(
            cookie -> cookie.contains("JSESSIONID=;")); //JESSSION=; -> 비어있으니까 invalidated 한거다.
        
        assertTrue(cookieInvalidated, "JSESSIONID cookie was invalidated");
    }
    
    @Test
    @DisplayName("Register new role user returns 201 CREATED")
    void whenRegisterNewRoleUserThenReturnCreatedStatus() {
        Faker faker = new Faker();
        
        // Create RegisterPostDTO object with test data
        com.cho.ecommerce.api.domain.RegisterRequestDTO registerRequestDTO = new com.cho.ecommerce.api.domain.RegisterRequestDTO();
        registerRequestDTO.setUsername("newuser");
        registerRequestDTO.setEmail("newuser@example.com");
        registerRequestDTO.setPassword("password");
        registerRequestDTO.setName("name");
        
        com.cho.ecommerce.api.domain.RegisterRequestDTOAddress address = new com.cho.ecommerce.api.domain.RegisterRequestDTOAddress();
        
        address.setStreet(faker.address().streetAddress());
        address.setCity(faker.address().city());
        address.setState(faker.address().state());
        address.setCountry(faker.address().country());
        address.setZipCode(faker.address().zipCode());
        
        registerRequestDTO.setAddress(address);
        
        // Convert RegisterPostDTO to HttpEntity
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<com.cho.ecommerce.api.domain.RegisterRequestDTO> request = new HttpEntity<>(
            registerRequestDTO, headers);
        
        // Perform POST request to register endpoint
        ResponseEntity<RegisterResponseDTO> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/register",
            request,
            RegisterResponseDTO.class
        );
        
        // Assert that the response status code is 201 CREATED
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        // Optionally, assert the response body content if necessary
        assertNotNull(response.getBody());
        assertEquals("Registration successful", response.getBody().getMessage());
    }
    
    @Transactional
    @Test
    void failedLoginAttemptsFiveTimesShouldLockUserAccount() {
        //given
        //유저 정보를 가져온다.
        UserEntity user = userRepository.findById(4L).get();
        assertTrue(user.getEnabled());
        
        //로그인 한다.
        ResponseEntity<String> firstLoginResponse = restTemplate.postForEntity(
            "http://localhost:" + port + "/login",
            createHeaders(user.getUsername(), "password"),
            String.class
        );
        
        //로그인 잘 되는지 확인
        assertEquals(HttpStatus.FOUND, firstLoginResponse.getStatusCode());
        String url = firstLoginResponse.getHeaders().getLocation().toString();
        assertEquals("http://localhost:" + port + "/user", url);
        
        //when
        //5 times of failed login attempt with wrong password
        for (int i = 0; i < SecurityConstants.MAX_LOGIN_ATTEMPTS; i++) {
            ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/login",
                createHeaders(user.getUsername(), "wrong-password-asdfasdfasdfsadg"),
                String.class
            );
        }
        
        //then
        //1. check account is locked
        entityManager.refresh(user);
        assertFalse(user.getEnabled());
        
        //2. Check if user sessions are invalidated
        List<SessionInformation> sessions = sessionRegistry.getAllSessions(user, false);
        assertTrue(sessions.isEmpty(), "User sessions should be empty after account lock");
        
        //3. check user authentication fails because the account is locked
        ResponseEntity<String> loginResponseThatShouldFail = restTemplate.postForEntity(
            "http://localhost:" + port + "/login",
            createHeaders(user.getUsername(), "password"),
            String.class
        );
        
        // Check that we are redirected to the login page
        assertEquals(HttpStatus.FOUND, loginResponseThatShouldFail.getStatusCode());
        String location = loginResponseThatShouldFail.getHeaders().getLocation().toString();
        assertEquals("http://localhost:" + port + "/login", location);
    }
}