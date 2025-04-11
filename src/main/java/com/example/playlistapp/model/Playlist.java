package com.example.playlistapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "playlists") // 'playlist' is a reserved word in some SQL dialects
public class Playlist {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String description;

  // Optional: associate with user
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User owner;

}
