package com.cho.ecommerce.global.config.redis;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


//spring boot app 실행할 때마다, redis와 spring security context에서 보관하던 세션 정보를 비운다.
@Component
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        SecurityContextHolder.clearContext(); // Clear the security context
        clearRedisCache(); // Clear Redis cache
    }
    
    private void clearRedisCache() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
