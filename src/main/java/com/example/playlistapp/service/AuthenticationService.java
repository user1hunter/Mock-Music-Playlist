package com.example.playlistapp.service;

import com.example.playlistapp.model.User;
import com.example.playlistapp.payload.LoginRequest;
import com.example.playlistapp.payload.SignupRequest;
import com.example.playlistapp.config.JwtUtils;
import com.example.playlistapp.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

  private final AuthenticationManager authenticationManager;
  private final JwtUtils jwtUtils;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public AuthenticationService(AuthenticationManager authenticationManager, JwtUtils jwtUtils,
      UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.authenticationManager = authenticationManager;
    this.jwtUtils = jwtUtils;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  // Register a new user
  public String register(SignupRequest signupRequest) {
    if (userRepository.existsByUsername(signupRequest.getUsername())) {
      throw new RuntimeException("Username already taken");
    }

    User user = new User();
    user.setUsername(signupRequest.getUsername());
    user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
    userRepository.save(user);

    return "User registered successfully!";
  }

  // Authenticate user and return JWT
  public String login(LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(),
            loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    return jwtUtils.generateJwtToken(authentication);
  }
}
