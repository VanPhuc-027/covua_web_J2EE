package com.group18.chessgame.controller;

import com.group18.chessgame.config.ActiveUserListener;
import com.group18.chessgame.model.Player;
import com.group18.chessgame.service.GameService;
import com.group18.chessgame.service.PlayerService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final GameService gameService;
    private final PlayerService playerService;

    @GetMapping("/")
    public String showLobbyPage(Model model, HttpSession session) {
        Player user = (Player) session.getAttribute("currentPlayer");
        if (user == null) return "redirect:/login";

        model.addAttribute("currentPlayer", user);
        model.addAttribute("waitingGames", gameService.getWaitingGame());
        model.addAttribute("topPlayers", playerService.getTopPlayers());
        model.addAttribute("onlineUsers", ActiveUserListener.getActiveSessionCount());

        return "index";
    }
}
