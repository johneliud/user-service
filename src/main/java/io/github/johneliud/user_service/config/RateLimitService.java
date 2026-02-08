package io.github.johneliud.user_service.config;

import io.bucket4j.Bandwidth;
import io.bucket4j.Bucket;
import io.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RateLimitService {
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    
    @Value("${rate.limit.login.capacity}")
    private int capacity;
    
    @Value("${rate.limit.login.refill.tokens}")
    private int refillTokens;
    
    @Value("${rate.limit.login.refill.minutes}")
    private int refillMinutes;

    public Bucket resolveBucket(String key) {
        return cache.computeIfAbsent(key, k -> createNewBucket());
    }

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(capacity, Refill.intervally(refillTokens, Duration.ofMinutes(refillMinutes)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    public boolean tryConsume(String key) {
        Bucket bucket = resolveBucket(key);
        boolean consumed = bucket.tryConsume(1);
        if (!consumed) {
            log.warn("Rate limit exceeded for key: {}", key);
        }
        return consumed;
    }
}