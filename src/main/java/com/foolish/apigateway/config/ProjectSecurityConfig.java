package com.foolish.apigateway.config;

import com.foolish.apigateway.filters.AuthenticationFilter;
import com.foolish.apigateway.services.AuthenticationService;
import com.foolish.apigateway.services.RedisService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
public class ProjectSecurityConfig {
  private final Environment env;
  private final RedisService redisService;
  private final AuthenticationService authService;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    http.addFilterAfter(new AuthenticationFilter(redisService, authService) , ExceptionTranslationFilter.class);
    http.csrf(AbstractHttpConfigurer::disable);
    http
            .authorizeHttpRequests(config -> config.anyRequest().permitAll());
    return http.build();
  }

  // CORS Configuration
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    String url = env.getProperty("CLIENT_URL", "http://localhost:5173");
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins(url).allowCredentials(true).exposedHeaders("*").allowedMethods("*");
      }
    };
  }
}
