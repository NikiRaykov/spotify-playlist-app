package com.example.spotifyplaylistapp.controller;

import com.example.spotifyplaylistapp.model.entity.binding.SongBindingModel;
import com.example.spotifyplaylistapp.model.entity.service.SongServiceModel;
import com.example.spotifyplaylistapp.model.entity.service.UserServiceModel;
import com.example.spotifyplaylistapp.service.SongService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/songs")
public class SongController {

    private final SongService songService;
    private final ModelMapper modelMapper;

    public SongController(SongService songService, ModelMapper modelMapper) {
        this.songService = songService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/add")
    public String add(Model model) {
        if (!model.containsAttribute("songBindingModel")) {
            model.addAttribute("songBindingModel", new SongBindingModel());
        }
        return "song-add";
    }

    @PostMapping("/add")
    public String confirmAdd(@Valid SongBindingModel songBindingModel,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("songBindingModel", songBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.songBindingModel", bindingResult);
            return "redirect:/songs/add";
        }

        UserServiceModel user = (UserServiceModel) session.getAttribute("user");

        songService.addSong(
                modelMapper.map(songBindingModel, SongServiceModel.class),
                user.getId()
        );

        redirectAttributes.addFlashAttribute("successMessage", "🎵 Song added successfully!");

        return "redirect:/";
    }

    @GetMapping("/remove/all")
    public String removeAll(RedirectAttributes redirectAttributes) {
        songService.removeAll();
        redirectAttributes.addFlashAttribute("successMessage", "🗑 All songs removed.");
        return "redirect:/";
    }

    @GetMapping("/playlist/add/{id}")
    public String addToPlaylist(@PathVariable Long id,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        UserServiceModel user = (UserServiceModel) session.getAttribute("user");
        songService.addToPlaylist(id, user.getId());

        redirectAttributes.addFlashAttribute("successMessage", "✅ Song added to playlist!");
        return "redirect:/";
    }

    @PostMapping("/remove")
    public String removeSongFromPlaylist(@RequestParam Long songId,
                                         HttpSession session,
                                         RedirectAttributes redirectAttributes) {
        UserServiceModel user = (UserServiceModel) session.getAttribute("user");
        songService.removeSongFromPlaylist(user.getId(), songId);

        redirectAttributes.addFlashAttribute("successMessage", "❌ Song removed from playlist.");
        return "redirect:/";
    }

    @ModelAttribute
    public SongBindingModel songBindingModel() {
        return new SongBindingModel();
    }
}
