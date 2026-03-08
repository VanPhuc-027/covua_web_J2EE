package com.group18.chessgame.controller;

import com.group18.chessgame.dto.GameResponse;
import com.group18.chessgame.dto.MoveRequest;
import com.group18.chessgame.service.GameLogicService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameLogicService gameService;

    public GameController(GameLogicService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/move")
    public GameResponse move(@RequestBody MoveRequest move) {
        return gameService.makeMove(move);
    }
    @GetMapping("/valid-moves")
    public List<int[]> getValidMoves(
            @RequestParam int row,
            @RequestParam int col
    ) {
        return gameService.getValidMoves(row, col);
    }
}

