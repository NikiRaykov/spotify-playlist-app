package com.example.spotifyplaylistapp.service;

import com.example.spotifyplaylistapp.model.entity.enums.StyleNameEnum;
import com.example.spotifyplaylistapp.model.entity.service.SongServiceModel;
import com.example.spotifyplaylistapp.model.entity.view.SongViewModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface SongService {
    void addSong(SongServiceModel songServiceModel, Long userId);

    void addToPlaylist(Long songId, Long userId);
    List<SongViewModel> getSongsInUserPlaylist(Long userId);

    BigDecimal getTotalDuration(Long userId);
    void removeAll();

    List<SongViewModel> findAllSongsByStyleName(StyleNameEnum styleNameEnum);

    List<SongViewModel> getSongsByUser(Long userId);

    void removeSongFromPlaylist(Long userId, Long songId);
}
