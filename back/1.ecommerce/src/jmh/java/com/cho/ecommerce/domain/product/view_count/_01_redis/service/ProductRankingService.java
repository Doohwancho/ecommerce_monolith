package com.cho.ecommerce.domain.product.view_count._01_redis.service;

import com.cho.ecommerce.domain.product.view_count._01_redis.domain.MockProduct;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductRankingService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PRODUCT_RANKING_KEY = "product:ranking";
    private static final int RANKING_LIMIT = 10;
    
    /**
     * Increment view count and update ranking
     */
    public void incrementView(MockProduct product, int viewCountDelta) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        // Update score in sorted set (score is the view count)
        zSetOps.incrementScore(PRODUCT_RANKING_KEY, product.getName(), viewCountDelta);
    }
    
    /**
     * Get top N viewed products
     */
    public List<String> getTopViewedProducts() {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        Set<Object> topProducts = zSetOps.reverseRange(PRODUCT_RANKING_KEY, 0, RANKING_LIMIT - 1);
        
        return topProducts.stream()
            .map(obj -> obj.toString())
            .collect(Collectors.toList());
    }
    
    /**
     * Get view count for a specific product
     */
//    public Double getViewCount(MockProduct product) {
//        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
//        Double score = zSetOps.score(PRODUCT_RANKING_KEY, product);
//        return score != null ? score : 0.0;
//    }
    
    /**
     * Get product rank (position in ranking)
     */
//    public Long getProductRank(MockProduct product) {
//        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
//        Long rank = zSetOps.reverseRank(PRODUCT_RANKING_KEY, product);
//        return rank != null ? rank + 1 : null;
//    }
    
    /**
     * Reset ranking for a product
     */
//    public void resetProductRanking(MockProduct product) {
//        redisTemplate.opsForZSet().remove(PRODUCT_RANKING_KEY, product);
//    }
    
    /**
     * Reset all rankings
     */
    public void resetAllRankings() {
        redisTemplate.delete(PRODUCT_RANKING_KEY);
    }
    
    /***************************************
     * performance enhanced version
     */
    /**
     * Increment view count and update ranking
     */
//    public void incrementView(MockProduct product, int viewCountDelta) {
//        // Use RedisCallback for better performance
//        redisTemplate.execute((RedisCallback<Object>) connection -> {
//            connection.zIncrBy(PRODUCT_RANKING_KEY.getBytes(), viewCountDelta, product.getName().getBytes());
//            return null;
//        });
//    }
    
    /**
     * Get top N viewed products
     */
//    public List<String> getTopViewedProducts() {
//        return redisTemplate.execute((RedisCallback<List<String>>) connection -> {
//            Set<byte[]> topProducts = connection.zRevRange(PRODUCT_RANKING_KEY.getBytes(), 0, RANKING_LIMIT - 1);
//            return topProducts.stream()
//                .map(String::new)
//                .collect(Collectors.toList());
//        });
//    }
    
    /**
     * Reset all rankings
     */
//    public void resetAllRankings() {
//        redisTemplate.execute((RedisCallback<Object>) connection -> {
//            connection.del(PRODUCT_RANKING_KEY.getBytes());
//            return null;
//        });
//    }

}