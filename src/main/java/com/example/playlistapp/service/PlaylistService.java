package com.example.playlistapp.service;

import com.example.playlistapp.model.Playlist;
import com.example.playlistapp.model.User;
import com.example.playlistapp.repository.PlaylistRepository;
import com.example.playlistapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PlaylistService {

  private final PlaylistRepository playlistRepository;
  private final UserRepository userRepository;

  public PlaylistService(PlaylistRepository playlistRepository, UserRepository userRepository) {
    this.playlistRepository = playlistRepository;
    this.userRepository = userRepository;
  }

  // Create new playlist
  public Playlist createPlaylist(String username, Playlist playlist) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));

    playlist.setOwner(user);
    return playlistRepository.save(playlist);
  }

  // Get all playlists for a specific user
  public List<Playlist> getUserPlaylists(String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));

    return playlistRepository.findByOwner(user);
  }

  // Get a specific playlist by ID
  public Playlist getPlaylistById(Long playlistId, String username) {
    Playlist playlist = playlistRepository.findById(playlistId)
        .orElseThrow(() -> new RuntimeException("Playlist not found"));

    if (!playlist.getOwner().getUsername().equals(username)) {
      throw new RuntimeException("You do not have permission to access this playlist");
    }

    return playlist;
  }

  // Update a playlist
  public Playlist updatePlaylist(Long playlistId, Playlist updatedPlaylist, String username) {
    Playlist playlist = playlistRepository.findById(playlistId)
        .orElseThrow(() -> new RuntimeException("Playlist not found"));

    if (!playlist.getOwner().getUsername().equals(username)) {
      throw new RuntimeException("You do not have permission to modify this playlist");
    }

    playlist.setName(updatedPlaylist.getName());
    playlist.setDescription(updatedPlaylist.getDescription());

    return playlistRepository.save(playlist);
  }

  // Delete a playlist
  public void deletePlaylist(Long playlistId, String username) {
    Playlist playlist = playlistRepository.findById(playlistId)
        .orElseThrow(() -> new RuntimeException("Playlist not found"));

    if (!playlist.getOwner().getUsername().equals(username)) {
      throw new RuntimeException("You do not have permission to delete this playlist");
    }

    playlistRepository.delete(playlist);
  }
}
