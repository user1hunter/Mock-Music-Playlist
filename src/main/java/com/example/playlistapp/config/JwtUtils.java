package com.example.playlistapp.config;

import io.jsonwebtoken.*;
import jakarta.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.*;
import org.springframework.stereotype.*;
import java.security.Key;
import java.util.Date;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

  @Value("${app.jwt.secret:secret1234567890secret1234567890}") // Override in application.properties
  private String jwtSecret;

  @Value("${app.jwt.expirationMs:86400000}") // 1 day
  private int jwtExpirationMs;

  private Key key;

  @PostConstruct
  public void init() {
    this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
  }

  public String generateJwtToken(Authentication authentication) {
    String username = authentication.getName();

    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public String getUsernameFromToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public boolean validateJwtToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      // You can log here if needed
      return false;
    }
  }
}
