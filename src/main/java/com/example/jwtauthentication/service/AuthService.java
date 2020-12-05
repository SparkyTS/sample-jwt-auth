package com.example.jwtauthentication.service;

import com.example.jwtauthentication.enums.RoleName;
import com.example.jwtauthentication.exception.AppException;
import com.example.jwtauthentication.model.Role;
import com.example.jwtauthentication.model.User;
import com.example.jwtauthentication.payload.JwtAuthenticationResponse;
import com.example.jwtauthentication.repository.RoleRepository;
import com.example.jwtauthentication.repository.UserRepository;
import com.example.jwtauthentication.security.JwtTokenProvider;
import com.example.jwtauthentication.security.UserPrincipal;
import java.util.Collections;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  final UserRepository userRepository;
  final RoleRepository roleRepository;
  final UserService userService;
  final JwtTokenProvider tokenProvider;
  final PasswordEncoder passwordEncoder;
  final AuthenticationManager authenticationManager;

  public AuthService(UserRepository userRepository, RoleRepository roleRepository, UserService userService,
    JwtTokenProvider tokenProvider, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.userService = userService;
    this.tokenProvider = tokenProvider;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
  }

  public JwtAuthenticationResponse signInUser(String usernameOrEmail, String password) {
    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(usernameOrEmail, password));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    return tokenProvider.generateToken(((UserPrincipal) authentication.getPrincipal()).getId());
  }

  public JwtAuthenticationResponse signUpUser(String name, String username, String email, String password) {

    // checking if username or email is not already in use
    userService.validateUniqueEmail(email);
    userService.validateUniqueUsername(username);

    // Creating user's account
    User user = new User(name, username, email, password);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    Role userRole = roleRepository
      .findByName(RoleName.ROLE_USER)
      .orElseThrow(() -> new AppException("User Role not set.", HttpStatus.INTERNAL_SERVER_ERROR));
    user.setRoles(Collections.singleton(userRole));
    user = userRepository.save(user);

    return tokenProvider.generateToken(user.getId());
  }

}
