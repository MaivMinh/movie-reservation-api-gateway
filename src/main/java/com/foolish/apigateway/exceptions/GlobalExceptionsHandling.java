package com.foolish.apigateway.exceptions;

import jakarta.servlet.ServletException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
@Order(1)
public class GlobalExceptionsHandling {


  @ExceptionHandler({ServletException.class})
  public ResponseEntity<ExceptionResponse> handleServletException(ServletException exception) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value())
            .body(new ExceptionResponse("Unauthenticated!", null));
  }

  @ExceptionHandler({RuntimeException.class})
  public ResponseEntity<ExceptionResponse> handleRuntimeException(RuntimeException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
            .body(new ExceptionResponse("Bad request", null));
  }
}
