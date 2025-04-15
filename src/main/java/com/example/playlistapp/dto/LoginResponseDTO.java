package com.example.playlistapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {
    private String jwt;
    private String spotifyToken;

    public LoginResponseDTO(String jwt, String spotifyToken) {
        this.jwt = jwt;
        this.spotifyToken = spotifyToken;
    }
}
