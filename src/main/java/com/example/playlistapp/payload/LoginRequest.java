package com.example.playlistapp.payload;

import lombok.Data;

@Data

public class LoginRequest {
  private String username;
  private String password;
}
