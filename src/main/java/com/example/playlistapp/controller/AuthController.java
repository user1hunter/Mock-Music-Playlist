package com.example.playlistapp.controller;

import com.example.playlistapp.model.User;
import com.example.playlistapp.payload.JwtResponse;
import com.example.playlistapp.payload.LoginRequest;
import com.example.playlistapp.payload.SignupRequest;
import com.example.playlistapp.repository.UserRepository;
import com.example.playlistapp.config.JwtUtils;
import com.example.playlistapp.config.SpotifyUtils;
import com.example.playlistapp.dto.LoginResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtils jwtUtils;
  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    try {

      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              loginRequest.getUsername(),
              loginRequest.getPassword()));

      SecurityContextHolder.getContext().setAuthentication(authentication);

      String jwt = jwtUtils.generateJwtToken(authentication);

      return ResponseEntity.ok(new LoginResponseDTO(jwt));
    } catch (Exception e) {
      logger.error("Authentication failed", e);
      return ResponseEntity.badRequest().body("Failed to authenticate user");
    }
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
    try {

      if (userRepository.existsByUsername(signupRequest.getUsername())) {
        return ResponseEntity.badRequest().body("Username is already taken");
      }

      User user = new User();
      user.setUsername(signupRequest.getUsername());
      user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
      userRepository.save(user);

      return ResponseEntity.ok("User registered successfully!");
    } catch (Exception e) {
      logger.error("Registration failed", e);
      return ResponseEntity.badRequest().body("Error registering user");
    }
  }
}
