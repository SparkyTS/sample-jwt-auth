package com.example.jwtauthentication.controller;

import com.example.jwtauthentication.payload.ApiResponse;
import com.example.jwtauthentication.payload.UserSummary;
import com.example.jwtauthentication.security.CurrentUser;
import com.example.jwtauthentication.security.UserPrincipal;
import com.example.jwtauthentication.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

  final UserService userService;

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/user/me")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<ApiResponse> getCurrentUser(@CurrentUser UserPrincipal currentUser) {
    UserSummary userSummary = new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getName());
    return ResponseEntity.ok(new ApiResponse(true, "Details Obtained Successfully!", userSummary));
  }

  @GetMapping("/user/validateUniqueUsername")
  public ResponseEntity<ApiResponse> checkUsernameAvailability(@RequestParam(value = "username") String username) {
    userService.validateUniqueUsername(username);
    return ResponseEntity.ok(new ApiResponse(true, "Yes, username is available !"));
  }

  @GetMapping("/user/")
  public ResponseEntity<ApiResponse> checkEmailAvailability(@RequestParam(value = "email") String email) {
    userService.validateUniqueEmail(email);
    return ResponseEntity.ok(new ApiResponse(true, "Yes, email is available !"));
  }
}
