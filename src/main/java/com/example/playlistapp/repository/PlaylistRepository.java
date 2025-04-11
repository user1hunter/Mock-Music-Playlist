package com.example.playlistapp.repository;

import com.example.playlistapp.model.Playlist;
import com.example.playlistapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

  List<Playlist> findByOwner(User owner);

}
