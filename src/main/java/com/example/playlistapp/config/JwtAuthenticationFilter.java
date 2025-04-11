package com.example.playlistapp.config;

import com.example.playlistapp.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtils jwtUtils;
  private final UserDetailsServiceImpl userDetailsService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");
    logger.debug("Authorization Header: " + authHeader);
    String token = null;
    String username = null;

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      token = authHeader.substring(7);
      logger.debug("Extracted Token: " + token);
      if (jwtUtils.validateJwtToken(token)) {
        username = jwtUtils.getUsernameFromToken(token);
        logger.debug("Extracted Username: " + username);
      } else {
        logger.warn("Invalid JWT token");
      }
    }

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      var userDetails = userDetailsService.loadUserByUsername(username);

      var authToken = new UsernamePasswordAuthenticationToken(
          userDetails, null, userDetails.getAuthorities());
      authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    filterChain.doFilter(request, response);
  }
}