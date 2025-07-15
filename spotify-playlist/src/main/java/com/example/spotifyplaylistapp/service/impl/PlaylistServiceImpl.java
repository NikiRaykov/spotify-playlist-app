package com.example.spotifyplaylistapp.service.impl;

import com.example.spotifyplaylistapp.model.entity.Playlist;
import com.example.spotifyplaylistapp.model.entity.Song;
import com.example.spotifyplaylistapp.model.entity.User;
import com.example.spotifyplaylistapp.repository.PlaylistRepository;
import com.example.spotifyplaylistapp.repository.SongRepository;
import com.example.spotifyplaylistapp.repository.UserRepository;
import com.example.spotifyplaylistapp.service.PlaylistService;
import org.springframework.stereotype.Service;

@Service
public class PlaylistServiceImpl implements PlaylistService {


    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public PlaylistServiceImpl(PlaylistRepository playlistRepository, SongRepository songRepository, UserRepository userRepository) {
        this.playlistRepository = playlistRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void addSongToPlaylist(Long songId, Long userId) {
        if (songId == null || userId == null) {
            throw new IllegalArgumentException("Song ID or User ID is null");
        }

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new IllegalArgumentException("Song not found with ID: " + songId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Playlist playlist = playlistRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Playlist newPlaylist = new Playlist();
                    newPlaylist.setUser(user);
                    return playlistRepository.save(newPlaylist);
                });

        if (!playlist.getSongs().contains(song)) {
            playlist.getSongs().add(song);
            playlistRepository.save(playlist);
        }
    }
}
