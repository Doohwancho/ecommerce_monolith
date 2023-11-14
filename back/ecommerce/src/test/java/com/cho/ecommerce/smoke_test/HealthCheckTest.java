package com.cho.ecommerce.smoke_test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.cho.ecommerce.Application;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = Application.class)
@ActiveProfiles("test")
@Tag("smoke") //to run, type "mvn test -Dgroups=smoke"
class HealthCheckTest {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void healthCheck() {
        ResponseEntity<String> entity = restTemplate.getForEntity(
            "http://localhost:" + port + "/actuator/health", String.class);
        assertThat(entity.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(entity.getBody()).contains("\"status\":\"UP\"");
    }
}