package com.foolish.apigateway.services;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.examples.lib.AuthenticateRequest;
import net.devh.boot.grpc.examples.lib.AuthenticateResponse;
import net.devh.boot.grpc.examples.lib.AuthenticateServiceGrpc;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@Service
public class AuthenticateServiceGrpcClient  extends AuthenticateServiceGrpc.AuthenticateServiceImplBase {
  private final AuthenticateServiceGrpc.AuthenticateServiceStub authenticateServiceStub;

  public AuthenticateServiceGrpcClient(Environment env) {
    String name = env.getProperty("AUTH_GRPC_SERVER_NAME", "localhost");
    int port = Integer.parseInt(Objects.requireNonNull(env.getProperty("AUTH_GRPC_SERVER_PORT", "9091")));
    System.out.println("gRPC server: " + name + ":" + port);
    ManagedChannel channel = ManagedChannelBuilder.forAddress(name, port).usePlaintext().build();
    this.authenticateServiceStub = AuthenticateServiceGrpc.newStub(channel);
  }

  public void doAuthenticate(String token, StreamObserver<AuthenticateResponse> responseObserver) {
    AuthenticateRequest request = AuthenticateRequest.newBuilder().setToken(token).build();
    authenticateServiceStub.doAuthenticate(request, responseObserver);
  }
}