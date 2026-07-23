package com.cinelog.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.registerCustomCache("movieDetails",
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofHours(24))
                        .maximumSize(5000)
                        .build());
        cacheManager.registerCustomCache("movieSearch",
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofHours(1))
                        .maximumSize(1000)
                        .build());
        return cacheManager;
    }
}
