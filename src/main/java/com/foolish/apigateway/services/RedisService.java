package com.foolish.apigateway.services;

import ch.qos.logback.core.util.Duration;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;

import java.util.Objects;

@Service
@AllArgsConstructor
public class RedisService {
  private final JedisPooled jedisPooled;

  public void saveWithTtl(String token, String userId, long ttl) {
    System.out.println("Executing saveWithTtl");
    jedisPooled.set(token, userId);
    jedisPooled.expire(token, ttl);
  }

  public Integer get(String key) {
    System.out.println("Executing get");
    return Integer.parseInt(Objects.requireNonNull(jedisPooled.get(key)));
  }
}
