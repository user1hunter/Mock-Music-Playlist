package com.example.playlistapp.dto;

public class SpotifyTracksDTO { 
    private String name;
    private String artist;

    public SpotifyTracksDTO(String name, String artist) {
        this.name = name;
        this.artist = artist;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

}
