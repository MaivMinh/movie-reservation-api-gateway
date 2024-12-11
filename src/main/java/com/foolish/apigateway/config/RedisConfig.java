package com.foolish.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPooled;

@Configuration
public class RedisConfig {
  @Bean
  public JedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6379);
    return new JedisConnectionFactory(config);
  }

  @Bean
  public JedisPooled jedisPooled() {
    return new JedisPooled("localhost", 6379);
  }

  @Bean
  public RedisTemplate<String, Integer> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Integer> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(connectionFactory);
    return redisTemplate;
  }
}
