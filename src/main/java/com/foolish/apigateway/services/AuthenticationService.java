package com.foolish.apigateway.services;

import lombok.AllArgsConstructor;
import net.devh.boot.grpc.examples.lib.AuthenticateResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class AuthenticationService {
  private final AuthenticateServiceGrpcClient authenticateServiceGrpcClient;

  public AuthenticateResponse doAuthenticate(String token) {
    return authenticateServiceGrpcClient.doAuthenticate(token);
  }
}
