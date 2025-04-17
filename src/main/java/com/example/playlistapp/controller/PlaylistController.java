package com.example.playlistapp.controller;

import com.example.playlistapp.config.SpotifyUtils;
import com.example.playlistapp.dto.PlaylistDTO;
import com.example.playlistapp.dto.SpotifyPlaylistDTO;
import com.example.playlistapp.dto.SpotifyTracksDTO;
import com.example.playlistapp.model.*;
import com.example.playlistapp.repository.*;
import lombok.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.security.core.annotation.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.web.bind.annotation.*;
import java.net.http.HttpClient;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.net.http.HttpRequest;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PlaylistController.class);

  private final PlaylistRepository playlistRepository;
  private final UserRepository userRepository;

  @GetMapping
  public List<PlaylistDTO> getAllPlaylists(@AuthenticationPrincipal UserDetails userDetails) {
    logger.info("GET /api/playlists user: {}", userDetails);
    try {
      Long ownerId = userRepository.findByUsername(userDetails.getUsername())
          .orElseThrow(() -> new RuntimeException("User not found"))
          .getId();
      logger.debug("Owner ID retrieved: {}", ownerId);

      List<PlaylistDTO> playlists = playlistRepository.findByOwnerId(ownerId).stream()
          .map(PlaylistDTO::new)
          .collect(Collectors.toList());
      logger.info("Playlists retrieved: {}", playlists);
      return playlists;
    } catch (Exception e) {
      logger.error("Error fetching playlists for user: {}", userDetails.getUsername(), e);
      throw new RuntimeException("Error fetching playlists");
    }
  }

  @PostMapping
  public ResponseEntity<PlaylistDTO> addPlaylist(@RequestBody Playlist playlist,
      @AuthenticationPrincipal UserDetails userDetails) {
    logger.info("POST /api/playlists user: {}", userDetails.getUsername());
    try {
      Long ownerId = userRepository.findByUsername(userDetails.getUsername())
          .orElseThrow(() -> new RuntimeException("User not found"))
          .getId();
      logger.debug("Owner ID retrieved: {}", ownerId);

      playlist.setOwnerId(ownerId);
      logger.debug("Playlist owner ID set: {}", playlist.getOwnerId());
      Playlist savedPlaylist = playlistRepository.save(playlist);
      logger.info("Playlist saved: {}", savedPlaylist);
      return ResponseEntity.ok(new PlaylistDTO(savedPlaylist));
    } catch (Exception e) {
      logger.error("Error creating playlist", userDetails.getUsername(), e);
      throw new RuntimeException("Error creating playlist");
    }
  }

  @GetMapping("/spotify")
  public ResponseEntity<?> getSpotifyPlaylists(@AuthenticationPrincipal UserDetails userDetails) {
    logger.info("GET /api/playlists/spotify user: {}", userDetails.getUsername());
    try {
      String accessToken = SpotifyUtils.getSpotifyAccessToken();
      logger.debug("Spotify Token generated for user: {}", userDetails.getUsername());

      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create("https://api.spotify.com/v1/users/314xqsp77xvfpta7e3uwaj6ury5e/playlists"))
          .header("Authorization", "Bearer " + accessToken)
          .header("Accept", "application/json")
          .GET()
          .build();

      logger.info("Request Headers: {}", request.headers());

      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        logger.debug("Spotify API response: {}", response.body());
        JSONObject jsonResponse = new JSONObject(response.body());
        JSONArray itemsArray = jsonResponse.getJSONArray("items");

        List<SpotifyPlaylistDTO> playlists = new ArrayList<>();
        for (int i = 0; i < itemsArray.length(); i++) {
          JSONObject item = itemsArray.getJSONObject(i);
          String playlistId = item.getString("id");
          String name = item.getString("name");
          playlists.add(new SpotifyPlaylistDTO(playlistId, name));
        }
        logger.info("Spotify playlists retrieved: {}", playlists);

        return ResponseEntity.ok(playlists.stream().limit(10).collect(Collectors.toList()));
      } else {
        logger.debug("Spotify API response status: {}", response.statusCode());
        logger.debug("Spotify API response body: {}", response.body());
        logger.debug("Access Token:", accessToken);
        throw new RuntimeException("Failed to fetch playlists: " + response.body());
      }
    } catch (Exception e) {
      logger.error("Error fetching categories",userDetails.getUsername(), e);
      throw new RuntimeException("Error fetching categories");
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getTracksByPlaylistId(@PathVariable String id,
      @AuthenticationPrincipal UserDetails userDetails) {
    logger.info("GET /api/playlists/{} user: {}", id, userDetails.getUsername());
    try {
      String accessToken = SpotifyUtils.getSpotifyAccessToken();
      logger.debug("Spotify Token generated for user: {}", userDetails.getUsername());

      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create("https://api.spotify.com/v1/playlists/" + id))
          .header("Authorization", "Bearer " + accessToken)
          .header("Accept", "application/json")
          .GET()
          .build();

      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        logger.debug("Spotify API response: {}", response.body());
        JSONObject jsonResponse = new JSONObject(response.body());
        JSONObject tracksObject = jsonResponse.getJSONObject("tracks");
        JSONArray itemsArray = tracksObject.getJSONArray("items");

        List<SpotifyTracksDTO> tracks = new ArrayList<>();
        for (int i = 0; i < itemsArray.length(); i++) {
          JSONObject item = itemsArray.getJSONObject(i);
          JSONObject track = item.getJSONObject("track");
          String songName = track.getString("name");
          JSONObject album = track.getJSONObject("album");
          JSONArray artist = album.getJSONArray("artists");
          String artistName = artist.getJSONObject(0).getString("name");
          tracks.add(new SpotifyTracksDTO(songName, artistName));
        }
        logger.info("Spotify tracks retrieved: {}", tracks);

        return ResponseEntity.ok(tracks.stream().limit(10).collect(Collectors.toList()));
      } else {
        logger.debug("Spotify API response status: {}", response.statusCode());
        logger.debug("Spotify API response body: {}", response.body());
        logger.debug("Access Token:", accessToken);
        throw new RuntimeException("Failed to fetch playlists: " + response.body());
      }
    } catch (Exception e) {
      logger.error("Error fetching categories", userDetails.getUsername(), e);
      throw new RuntimeException("Error fetching categories");
    }
  }

  @DeleteMapping("/{playlistId}")
  public ResponseEntity<Void> deletePlaylist(@PathVariable Long playlistId,
      @AuthenticationPrincipal UserDetails userDetails) {
    logger.info("DELETE /api/playlists/{} user: {}", playlistId, userDetails.getUsername());
    try {
      Playlist playlist = playlistRepository.findById(playlistId)
          .orElseThrow(() -> new RuntimeException("Playlist not found"));
      logger.debug("Playlist retrieved: {}", playlist);

      Long ownerId = userRepository.findByUsername(userDetails.getUsername())
          .orElseThrow(() -> new RuntimeException("User not found"))
          .getId();
      logger.debug("Owner ID retrieved: {}", ownerId);

      if (!playlist.getOwnerId().equals(ownerId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }
      logger.debug("Playlist owner ID matches: {}", playlist.getOwnerId());

      playlistRepository.delete(playlist);
      logger.info("Playlist deleted: {}", playlistId);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      logger.error("Error deleting playlist",userDetails.getUsername(), e);
      throw new RuntimeException("Error deleting playlist");
    }
  }
}
