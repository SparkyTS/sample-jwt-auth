package com.example.jwtauthentication.service;

import com.example.jwtauthentication.exception.AppException;
import com.example.jwtauthentication.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void validateUniqueUsername(String username) {
    if (userRepository.existsByUsername(username)) {
      throw new AppException("Username is already taken!", HttpStatus.BAD_REQUEST);
    }
  }

  public void validateUniqueEmail(String email) {
    if (userRepository.existsByEmail(email)) {
      throw new AppException("Email address is already in use!", HttpStatus.BAD_REQUEST);
    }
  }
}
