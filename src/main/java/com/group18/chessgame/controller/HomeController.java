package com.group18.chessgame.controller;

import com.group18.chessgame.model.Game;
import com.group18.chessgame.model.Player;
import com.group18.chessgame.service.GameService;
import com.group18.chessgame.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final GameService gameService;
    private final PlayerService playerService;

    @GetMapping("/")
    public String showLobbyPage(Model model) {
        List<Game> waitingGames = gameService.getWaitingGame();
        List<Player> topPlayers = playerService.getTopPlayers();

        model.addAttribute("waitingGames", waitingGames);
        model.addAttribute("topPlayers", topPlayers);

        return "index";
    }
}
