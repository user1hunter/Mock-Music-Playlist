package com.example.playlistapp.controller;

import com.example.playlistapp.dto.PlaylistDTO;
import com.example.playlistapp.model.*;
import com.example.playlistapp.repository.*;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PlaylistController.class);

  private final PlaylistRepository playlistRepository;
  private final UserRepository userRepository;

  @GetMapping
  public List<Playlist> getAllPlaylists(@AuthenticationPrincipal UserDetails userDetails) {
    try {
      com.example.playlistapp.model.User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
      return user.getPlaylists().stream().toList();
    } catch (Exception e) {
      logger.error("Error fetching playlists", e);
      throw new RuntimeException("Error fetching playlists");
    }
  }

  @PostMapping
  public ResponseEntity<PlaylistDTO> createPlaylist(@RequestBody Playlist playlist,
      @AuthenticationPrincipal UserDetails userDetails) {
    try {
      com.example.playlistapp.model.User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
      playlist.setOwner(user);
      Playlist savedPlaylist = playlistRepository.save(playlist);
      return ResponseEntity.ok(new PlaylistDTO(savedPlaylist));
    } catch (Exception e) {
      logger.error("Error creating playlist", e);
      throw new RuntimeException("Error creating playlist");
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<PlaylistDTO> getById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
    try {
      Playlist playlist = playlistRepository.findById(id).orElseThrow(EntityNotFoundException::new);
      if (!playlist.getOwner().getUsername().equals(userDetails.getUsername())) {
        return ResponseEntity.status(403).build();
      }
      return ResponseEntity.ok(new PlaylistDTO(playlist));
    } catch (Exception e) {
      logger.error("Error fetching playlist", e);
      throw new RuntimeException("Error fetching playlist");
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<PlaylistDTO> updatePlaylist(@PathVariable Long id, @RequestBody Playlist updatedPlaylist,
      @AuthenticationPrincipal UserDetails userDetails) {
    try {
      Playlist playlist = playlistRepository.findById(id).orElseThrow(EntityNotFoundException::new);
      if (!playlist.getOwner().getUsername().equals(userDetails.getUsername())) {
        return ResponseEntity.status(403).build();
      }

      playlist.setName(updatedPlaylist.getName());
      playlist.setDescription(updatedPlaylist.getDescription());

      Playlist saved = playlistRepository.save(playlist);
      return ResponseEntity.ok(new PlaylistDTO(saved));
    } catch (Exception e) {
      logger.error("Error updating playlist", e);
      throw new RuntimeException("Error updating playlist");
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePlaylist(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
    try {
      Playlist playlist = playlistRepository.findById(id).orElseThrow(EntityNotFoundException::new);
      if (!playlist.getOwner().getUsername().equals(userDetails.getUsername())) {
        return ResponseEntity.status(403).build();
      }

      playlistRepository.delete(playlist);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      logger.error("Error deleting playlist", e);
      throw new RuntimeException("Error deleting playlist");
    }
  }
}
