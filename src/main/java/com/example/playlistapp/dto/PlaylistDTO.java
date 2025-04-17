package com.example.playlistapp.dto;

import com.example.playlistapp.model.Playlist;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaylistDTO {
    private Long id;
    private String name;
    private String spotifyPlaylistId;

    public PlaylistDTO(Playlist playlist) {
        this.id = playlist.getId();
        this.name = playlist.getName();
        this.spotifyPlaylistId = playlist.getSpotifyPlaylistId();
    }
}
