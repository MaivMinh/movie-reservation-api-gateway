package com.foolish.apigateway.filters;

import com.foolish.apigateway.services.AuthenticationService;
import com.foolish.apigateway.services.RedisService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.examples.lib.AuthenticateResponse;
import org.checkerframework.checker.units.qual.A;
import org.springframework.cglib.core.internal.Function;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.function.ServerRequest;

import java.io.IOException;

@Slf4j
@Configuration
@AllArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {
  private final RedisService redisService;
  private final AuthenticationService authService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    log.info("ENTERING AUTHENTICATION FILTER");
    // request tồn tại Authorization header.
    String token = this.getAuthorizationToken(request);
    // Kiểm tra token có trong redis không
    Integer value = redisService.get(token);
    if (value != null) attachUserIdToRequest(value.toString());
    else {
      // Token không tồn tại trong redis, gửi request đến service authenticate
      AuthenticateResponse aResponse = authService.doAuthenticate(token);
      if (!aResponse.getIsValid()) {
        throw new ServletException("Invalid token");
      }
      String userId = aResponse.getUserId();
      long ttl = aResponse.getTtl();
      redisService.saveWithTtl(token, userId, ttl);
      attachUserIdToRequest(userId);
    }
    filterChain.doFilter(request, response);
  }

  private String getAuthorizationToken(ServletRequest request) {
    String authorization = ((HttpServletRequest) request).getHeader("Authorization");
    return authorization.substring(7);
  }

  public static Function<ServerRequest, ServerRequest> attachUserIdToRequest(String userId) {
    return request -> ServerRequest.from(request).header("X-USER-ID", userId).build();
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    return request.getHeader("Authorization") == null;
  }
}
