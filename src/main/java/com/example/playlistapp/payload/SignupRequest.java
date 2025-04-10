package com.example.playlistapp.payload;

import lombok.Data;

@Data
public class SignupRequest {
  private String username;
  private String password;
}
