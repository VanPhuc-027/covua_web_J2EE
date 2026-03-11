package com.group18.chessgame.controller;

import com.group18.chessgame.enums.GameMode;
import com.group18.chessgame.enums.GameStatus;
import com.group18.chessgame.model.Board;
import com.group18.chessgame.model.Game;
import com.group18.chessgame.model.Player;
import com.group18.chessgame.service.GameService;
import com.group18.chessgame.utils.FenUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameLobbyController {
    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/create")
    public String createGame(HttpSession session) {
        Player creator = (Player) session.getAttribute("currentPlayer");
        if (creator == null) return "redirect:/login";
        Game newGame = gameService.createGame(creator, GameMode.Player_VS_Player);
        simpMessagingTemplate.convertAndSend("/topic/lobby", "ROOM_CREATED:" + creator.getUsername());
        return "redirect:/game/" + newGame.getId();
    }

    @PostMapping("/join")
    public String joinGame(@RequestParam String gameId, HttpSession session) {
        Player player = (Player) session.getAttribute("currentPlayer");
        if (player == null) return "redirect:/login";
        Game joinGame = gameService.joinGame(gameId, player);
        if (joinGame == null) return "redirect:/";
        simpMessagingTemplate.convertAndSend("/topic/game/" + gameId, "PLAYER_JOINED");
        return "redirect:/game/" + gameId;
    }

    @PostMapping("/cancel")
    public String cancelGame(@RequestParam String gameId, HttpSession session) {
        Player player = (Player) session.getAttribute("currentPlayer");
        if (player == null) return "redirect:/login";
        Game game = gameService.getGame(gameId);
        if (game != null && game.getStatus() == GameStatus.WAITING) {
            boolean isPlayerInRoom = (game.getWhitePlayer() != null && game.getWhitePlayer().getId() == player.getId()) ||
                    (game.getBlackPlayer() != null && game.getBlackPlayer().getId() == player.getId());
            if (isPlayerInRoom) {
                gameService.removeGame(gameId);
                simpMessagingTemplate.convertAndSend("/topic/lobby", "ROOM_CANCELLED:" + player.getUsername());
            }
        }
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String showGameRoom(@PathVariable String id, Model model, HttpSession session) {
        Player currentPlayer = (Player) session.getAttribute("currentPlayer");
        if (currentPlayer == null) return "redirect:/login";
        Game game = gameService.getGame(id);
        if (game == null) return "redirect:/";
        Board board = new Board();
        FenUtils.fenToBoard(game.getCurrentFen(), board);
        game.setBoard(board);
        model.addAttribute("currentPlayer", currentPlayer);
        model.addAttribute("game", game);
        model.addAttribute("board", board);
        return "game";
    }
}