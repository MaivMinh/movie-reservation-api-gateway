package com.foolish.apigateway.config;

import com.foolish.apigateway.services.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@AllArgsConstructor
public class ProjectSecurityConfig {
  private final Environment env;
  private final AuthenticationService authService;

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(
          ServerHttpSecurity http) {

    return http.authorizeExchange(exchanges -> exchanges
                    .anyExchange().permitAll())
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .build();
  }


  // CORS Configuration
  @Bean
  CorsWebFilter corsFilter() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.addAllowedOrigin("http://localhost");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return new CorsWebFilter(source);
  }
}
