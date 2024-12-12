package com.foolish.apigateway.filters;

import com.foolish.apigateway.services.AuthenticationService;
import com.foolish.apigateway.services.RedisService;
import net.devh.boot.grpc.examples.lib.AuthenticateResponse;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

/*Không thực hiện cấu hình ở đây. Bởi vì khi filter này implement lại GlobalFilter thì sẽ được tự động thêm vào Filter chains*/
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {
  private final AuthenticationService authenticationService;
  private final RedisService redisService;

  public AuthenticationFilter(AuthenticationService authenticationService, RedisService redisService) {
    this.authenticationService = authenticationService;
    this.redisService = redisService;
  }

  @Override
  public int getOrder() {
    return -1;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    System.out.println("AuthenticationFilter");
    String authorization = exchange.getRequest().getHeaders().getFirst("Authorization");
    if (authorization != null) {
      String token = authorization.substring(7);
      // Tìm token trong Redis
      String value = null;
      try {
        value = redisService.get(token);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      if (value != null && !value.isEmpty()) {
        ServerHttpRequest request = exchange.getRequest()
                .mutate()
                .header("X-USER-ID", value)
                .build();
        ServerWebExchange updated = exchange.mutate().request(request).build();
        return chain.filter(updated);
      } else {
        AuthenticateResponse response = null;
        try {
          response = authenticationService.doAuthenticate(token);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
        if (!response.getIsValid()) {
          exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
          return exchange.getResponse().setComplete();
        }
        String user_id = response.getUserId();
        long ttl = response.getTtl();
        redisService.set(token, user_id, ttl);
        ServerHttpRequest request = exchange.getRequest()
                .mutate()
                .header("X-USER-ID", user_id)
                .build();
        ServerWebExchange updated = exchange.mutate().request(request).build();
        return chain.filter(updated);
      }
    }
    return chain.filter(exchange);
  }
}
