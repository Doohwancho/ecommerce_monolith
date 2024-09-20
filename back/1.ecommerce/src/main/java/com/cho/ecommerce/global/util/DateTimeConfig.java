package com.cho.ecommerce.global.util;

import java.time.LocalDateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DateTimeConfig {
    
    @Bean
    public LocalDateTime startTimeForDiscount() {
        return LocalDateTime.now();
    }
    
    @Bean
    public LocalDateTime endTimeForDiscount() {
        return LocalDateTime.now().plusDays(30);
    }
}
