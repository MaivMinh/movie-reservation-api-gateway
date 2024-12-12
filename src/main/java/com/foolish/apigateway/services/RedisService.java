package com.foolish.apigateway.services;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
public class RedisService {
  private final ReactiveRedisOperations<String, String> redisOperations;

  public String get(String key) throws ExecutionException, InterruptedException {
    return redisOperations.opsForValue().get(key).toFuture().get();
  }

  public Mono<Boolean> set(String key, String value, long ttl) {
     return redisOperations.opsForValue().set(key, value, Duration.ofSeconds(ttl));
  }
}
