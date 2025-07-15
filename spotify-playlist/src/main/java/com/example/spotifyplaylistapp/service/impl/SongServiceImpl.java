package com.example.spotifyplaylistapp.service.impl;

import com.example.spotifyplaylistapp.model.entity.Playlist;
import com.example.spotifyplaylistapp.model.entity.Song;
import com.example.spotifyplaylistapp.model.entity.enums.StyleNameEnum;
import com.example.spotifyplaylistapp.model.entity.service.SongServiceModel;
import com.example.spotifyplaylistapp.model.entity.service.StyleServiceModel;
import com.example.spotifyplaylistapp.model.entity.view.SongViewModel;
import com.example.spotifyplaylistapp.repository.PlaylistRepository;
import com.example.spotifyplaylistapp.repository.SongRepository;
import com.example.spotifyplaylistapp.repository.UserRepository;
import com.example.spotifyplaylistapp.service.SongService;
import com.example.spotifyplaylistapp.service.StyleService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SongServiceImpl implements SongService {

    private final StyleService styleService;
    private final SongRepository songRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;


    public SongServiceImpl(StyleService styleService, SongRepository songRepository, ModelMapper modelMapper, UserRepository userRepository, UserRepository userRepository1, PlaylistRepository playlistRepository) {
        this.styleService = styleService;
        this.songRepository = songRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository1;
        this.playlistRepository = playlistRepository;
    }

    @Override
    public void addSong(SongServiceModel songServiceModel, Long userId) {
        StyleServiceModel styleServiceModel = this.styleService
                .findByStyleNameEnum(songServiceModel.getStyle().getName());

        songServiceModel.setStyle(styleServiceModel);

        Song song = this.modelMapper.map(songServiceModel, Song.class);
        song.setUser(userRepository.findById(userId).orElseThrow());

        this.songRepository.save(song);
    }

    @Override
    public void addToPlaylist(Long songId, Long userId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new IllegalArgumentException("Song not found"));

        Playlist playlist = playlistRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Playlist newPlaylist = new Playlist();
                    newPlaylist.setUser(userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("User not found")));
                    return newPlaylist;
                });

        if (!playlist.getSongs().contains(song)) {
            playlist.getSongs().add(song);
        }


        playlistRepository.save(playlist);

    }

    @Override
    public List<SongViewModel> getSongsInUserPlaylist(Long userId) {
        return playlistRepository.findByUserId(userId)
                .map(playlist -> playlist.getSongs().stream()
                        .map(song -> modelMapper.map(song, SongViewModel.class))
                        .collect(Collectors.toList()))
                .orElse(List.of());

    }

    @Override
    public BigDecimal getTotalDuration(Long userId) {
        return playlistRepository.findByUserId(userId)
                .map(playlist -> playlist.getSongs().stream()
                        .map(Song::getDuration)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .orElse(BigDecimal.ZERO);
    }


    @Override
    public void removeAll() {
        List<Playlist> allPlaylists = playlistRepository.findAll();

        for (Playlist playlist: allPlaylists) {
            playlist.getSongs().clear();
        }

        playlistRepository.saveAll(allPlaylists);

        songRepository.deleteAll();
    }

    @Override
    public List<SongViewModel> findAllSongsByStyleName(StyleNameEnum styleNameEnum) {
        return songRepository.findAllByStyle_Name(styleNameEnum)
                .stream()
                .map(song -> modelMapper.map(song, SongViewModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<SongViewModel> getSongsByUser(Long userId) {
        return songRepository.findAll()
                .stream()
                .filter(Objects::nonNull)
                .map(song -> modelMapper.map(song, SongViewModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public void removeSongFromPlaylist(Long userId, Long songId) {
        Playlist playlist = playlistRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found"));

        playlist.setSongs(
                playlist.getSongs().stream()
                        .filter(song -> !song.getId().equals(songId))
                        .collect(Collectors.toList())
        );

        playlistRepository.save(playlist);
    }

}
