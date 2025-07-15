package com.example.spotifyplaylistapp.controller;

import com.example.spotifyplaylistapp.model.entity.service.UserServiceModel;
import com.example.spotifyplaylistapp.service.PlaylistService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/playlist")
public class PlaylistController {

    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping("/add/{id}")
    public String addSongToPlaylist(@PathVariable Long id, HttpSession session) {
        UserServiceModel user = (UserServiceModel) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        playlistService.addSongToPlaylist(id, user.getId());

        return "redirect:/";
    }
}
