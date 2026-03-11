package com.group18.chessgame.controller;

import com.group18.chessgame.dto.GameResponse;
import com.group18.chessgame.dto.MoveRequest;
import com.group18.chessgame.model.Board;
import com.group18.chessgame.service.GameLogicService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final GameLogicService gameLogicService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/{gameId}/move")
    public GameResponse move(
            @PathVariable String gameId,
            @RequestBody MoveRequest moveRequest) {

        GameResponse response = gameLogicService.makeMove(gameId, moveRequest);

        if (response.isSuccess()) {
            simpMessagingTemplate.convertAndSend("/topic/game/" + gameId, "BOARD_UPDATED");
        }

        return response;
    }

    @GetMapping("/{gameId}/valid-moves")
    public List<int[]> getValidMoves(
            @PathVariable String gameId,
            @RequestParam int row,
            @RequestParam int col) {

        return gameLogicService.getValidMoves(gameId, row, col);
    }

    @GetMapping("/{gameId}/board")
    public Board getBoard(@PathVariable String gameId) {
        return gameLogicService.getBoard(gameId);
    }
}