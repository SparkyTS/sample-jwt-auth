package com.example.jwtauthentication.controller;

import javax.validation.Valid;

import com.example.jwtauthentication.payload.JwtAuthenticationResponse;
import com.example.jwtauthentication.payload.LoginRequest;
import com.example.jwtauthentication.payload.SignUpRequest;
import com.example.jwtauthentication.repository.UserRepository;
import com.example.jwtauthentication.security.JwtTokenProvider;
import com.example.jwtauthentication.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

  final AuthService authService;
  final JwtTokenProvider tokenProvider;
  final UserRepository userRepository;

  public AuthController(UserRepository userRepository, AuthService authService, JwtTokenProvider tokenProvider) {
    this.userRepository = userRepository;
    this.authService = authService;
    this.tokenProvider = tokenProvider;
  }

  @PostMapping("/signin")
  public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    return ResponseEntity.ok(authService.signInUser(loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));
  }

  @PostMapping("/signup")
  public ResponseEntity<JwtAuthenticationResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
    return new ResponseEntity<>(
      authService.signUpUser(signUpRequest.getName(), signUpRequest.getUsername(), signUpRequest.getEmail(),
        signUpRequest.getPassword()), HttpStatus.CREATED);
  }

  @PostMapping("/refreshToken")
  public ResponseEntity<JwtAuthenticationResponse> refreshToken(
    @RequestBody JwtAuthenticationResponse jwtAuthenticationResponse) {
    return ResponseEntity.ok(tokenProvider.generateNewTokens(jwtAuthenticationResponse.getRefreshToken()));
  }
}