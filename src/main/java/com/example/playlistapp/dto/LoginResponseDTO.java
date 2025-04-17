package com.example.playlistapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {
    private String jwt;

    public LoginResponseDTO(String jwt) {
        this.jwt = jwt;
    }
}
