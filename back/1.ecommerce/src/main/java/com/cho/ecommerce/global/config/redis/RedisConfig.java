package com.cho.ecommerce.global.config.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800) // 30 minutes
public class RedisConfig {
    
    @Value("${spring.redis.host}")
    private String host;
    
    @Value("${spring.redis.port}")
    private int port;
    
    private RedisCacheManager redisCacheManager;
    
    @Bean
    public LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // JSON 직렬화 설정
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(
            Object.class);
        ObjectMapper mapper = new ObjectMapper();
        
        // JSR-310 (Java 8 Date & Time API) 지원 추가
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(mapper);
        
        // key는 String, value는 JSON
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        
        template.setDefaultSerializer(serializer);
        template.afterPropertiesSet();
        
        return template;
    }
    
    //aws redis와 연결시, CONFIG라는 명령어를 aws elastic cache redis가 허용하지 않아서 생기는 문제를 레디스 클라이언트 라이브러리에서 레디스 서버로 CONFIG 명령어를 날리지 않도록 비활성화하는 방법으로 해결
    @Bean
    public ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }
    
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL);
        
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1))
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(
                SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(mapper)));
        
        this.redisCacheManager = RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(config)
            .build();
        
        return this.redisCacheManager;
    }
    
    //Note)
    //lazy loading vs write through
    //1. lazy loading
    //db에 write하는 것과 별개로, read 요청이 왔을 때, redis에 cache를 저장하고 뿌리면서 1시간 마다 캐시를 지워준다.
    //cons-1. slow initial request
    //cons-2. possibility of serving stale data (thats why I delete cache every hour)
    
    //2. write through
    //db에 write할 때 cache를 업데이트 하는 방식
    //cache always contains the most recent data
    //cons-1. slower write to db
    //cons-2. more complex implementation compared to Lazy Loading
    //Requires careful error handling to maintain consistency (e.g., if a write to the database succeeds but the cache update fails).
    //how to implement?
    //1. 매 write마다 cache가 새롭게 갱신되니까, @Schedule(1hr)로 1시간마다 캐시 지우는 코드 필요 없음
    //2. write하는 코드에 @CachePut(value = "topTenRatedProductsCached") 사용
    //3. 만약 delete하는 코드가 있다면 @CacheEvict(value = "topTenRatedProductsCached", allEntries = true) 로 캐시 지워줘서 repopulate 유도
    
    // @Scheduled(cron = "0 0 0 * * ?") // OR using a cron expression, for example, every day at midnight
    @Scheduled(fixedRate = 3600000) // 3600000 milliseconds = 1 hour (Run every hour)
    public void clearTopTenRatedProductsCache() {
        if (this.redisCacheManager != null) {
            Cache cache = this.redisCacheManager.getCache(
                "topTenRatedProductsCached"); //main page에 top 10 rated products를 매 시간마다 캐시에 새로 갱신
            if (cache != null) {
                cache.clear();
            }
        }
    }
}
