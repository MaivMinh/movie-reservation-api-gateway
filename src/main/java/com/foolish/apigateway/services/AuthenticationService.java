package com.foolish.apigateway.services;

import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import net.devh.boot.grpc.examples.lib.AuthenticateResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
public class AuthenticationService {
  private final AuthenticateServiceGrpcClient authenticateServiceGrpcClient;

  public AuthenticateResponse doAuthenticate(String token) throws ExecutionException, InterruptedException {
    return (AuthenticateResponse) Mono.create(sink -> {
      authenticateServiceGrpcClient.doAuthenticate(token, new StreamObserver<>() {
        @Override
        public void onNext(AuthenticateResponse value) {
          sink.success(value);
        }

        @Override
        public void onError(Throwable t) {
          sink.error(t);
        }

        @Override
        public void onCompleted() {
          sink.success();
        }
      });
    }).toFuture().get();
  }
}
