package com.group18.chessgame.controller;

import com.group18.chessgame.model.Player;
import com.group18.chessgame.service.GameLobbyService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final GameLobbyService gameLobbyService;

    @GetMapping("/profile")
    public String showProfilePage(Model model, HttpSession session, @RequestParam(defaultValue = "0") int page) {
        Player user = (Player) session.getAttribute("currentPlayer");
        if (user == null) return "redirect:/login";

        model.addAttribute("currentPlayer", user);
        model.addAttribute("gameHistory", gameLobbyService.getGameHistory(user.getId(), PageRequest.of(page, 5)));
        model.addAttribute("totalWins", gameLobbyService.getTotalWins(user.getId()));
        model.addAttribute("totalLosses", gameLobbyService.getTotalLosses(user.getId()));
        return "profile";
    }

    @GetMapping("/settings")
    public String showSettingsPage() {
        return "settings";
    }
}
