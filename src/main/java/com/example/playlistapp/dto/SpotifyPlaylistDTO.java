package com.example.playlistapp.dto;

public class SpotifyPlaylistDTO {
    private String id;
    private String name;

    public SpotifyPlaylistDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
