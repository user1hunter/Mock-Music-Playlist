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
    try {
      Long ownerId = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

      return playlistRepository.findByOwnerId(ownerId).stream()
                .map(PlaylistDTO::new)
                .collect(Collectors.toList());

    } catch (Exception e) {
      logger.error("Error fetching playlists", e);
      throw new RuntimeException("Error fetching playlists");
    }
  }

  @PostMapping
  public ResponseEntity<PlaylistDTO> addPlaylist(@RequestBody Playlist playlist,
      @AuthenticationPrincipal UserDetails userDetails) {
    try {
      Long ownerId = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
                
      playlist.setOwnerId(ownerId);
      Playlist savedPlaylist = playlistRepository.save(playlist);
      return ResponseEntity.ok(new PlaylistDTO(savedPlaylist));
    } catch (Exception e) {
      logger.error("Error creating playlist", e);
      throw new RuntimeException("Error creating playlist");
    }
  }

  @GetMapping("/spotify")
  public ResponseEntity<?> getSpotifyPlaylists(@AuthenticationPrincipal UserDetails userDetails) {
        try {
          String accessToken = SpotifyUtils.getSpotifyAccessToken();
  
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
  
              List<SpotifyPlaylistDTO> categories = new ArrayList<>();
              for (int i = 0; i < itemsArray.length(); i++) {
                  JSONObject item = itemsArray.getJSONObject(i);
                  String playlistId = item.getString("id");
                  String name = item.getString("name");
                  categories.add(new SpotifyPlaylistDTO(playlistId, name));
              }

              return ResponseEntity.ok(categories.stream().limit(10).collect(Collectors.toList()));
          } else {
              logger.debug("Spotify API response status: {}", response.statusCode());
              logger.debug("Spotify API response body: {}", response.body());
              logger.debug("Access Token:", accessToken);
              throw new RuntimeException("Failed to fetch playlists: " + response.body());
          }
      } catch (Exception e) {
          logger.error("Error fetching categories", e);
          throw new RuntimeException("Error fetching categories");
      }
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getTracksByPlaylistId(@PathVariable String id,
      @AuthenticationPrincipal UserDetails userDetails) {
        try {
          String accessToken = SpotifyUtils.getSpotifyAccessToken();
  
          HttpRequest request = HttpRequest.newBuilder()
                  .uri(URI.create("https://api.spotify.com/v1/playlists/" + id))
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

              return ResponseEntity.ok(tracks.stream().limit(10).collect(Collectors.toList()));
          } else {
              logger.debug("Spotify API response status: {}", response.statusCode());
              logger.debug("Spotify API response body: {}", response.body());
              logger.debug("Access Token:", accessToken);
              throw new RuntimeException("Failed to fetch playlists: " + response.body());
          }
      } catch (Exception e) {
          logger.error("Error fetching categories", e);
          throw new RuntimeException("Error fetching categories");
      }
  }

  @DeleteMapping("/{playlistId}")
  public ResponseEntity<Void> deletePlaylist(@PathVariable Long playlistId, @AuthenticationPrincipal UserDetails userDetails) {
    try {
      Playlist playlist = playlistRepository.findById(playlistId)
          .orElseThrow(() -> new RuntimeException("Playlist not found"));

      Long ownerId = userRepository.findByUsername(userDetails.getUsername())
          .orElseThrow(() -> new RuntimeException("User not found"))
          .getId();

      if (!playlist.getOwnerId().equals(ownerId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }

      playlistRepository.delete(playlist);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      logger.error("Error deleting playlist", e);
      throw new RuntimeException("Error deleting playlist");
    }
  }
}
