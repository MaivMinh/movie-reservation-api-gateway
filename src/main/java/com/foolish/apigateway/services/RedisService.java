package com.foolish.apigateway.services;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.Duration;

@Service
@AllArgsConstructor
public class RedisService {
  private final ReactiveRedisOperations<String, String> redisOperations;

  public String get(String key){
    try {
      return redisOperations.opsForValue().get(key).toFuture().get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public Mono<Boolean> set(String key, String value, long ttl) {
     return redisOperations.opsForValue().set(key, value, Duration.ofSeconds(ttl));
  }
}
