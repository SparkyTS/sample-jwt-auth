package com.example.jwtauthentication.exception;

import com.example.jwtauthentication.payload.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@EnableWebMvc
public class ExceptionController extends ResponseEntityExceptionHandler {

  @ExceptionHandler(AppException.class)
  public ResponseEntity<ApiResponse> handleAppException(AppException appException) {
    return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, appException.getMessage()),
      appException.getHttpStatus());
  }
}
