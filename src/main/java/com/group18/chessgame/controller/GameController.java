package com.group18.chessgame.controller;

import com.group18.chessgame.dto.GameResponse;
import com.group18.chessgame.dto.MoveRequest;
import com.group18.chessgame.model.Board;
import com.group18.chessgame.service.GameLogicService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import com.group18.chessgame.repository.MoveRepository;
import com.group18.chessgame.model.Move;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final GameLogicService gameLogicService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MoveRepository moveRepository;

    @PostMapping("/{gameId}/move")
    public GameResponse move(
            @PathVariable String gameId,
            @RequestBody MoveRequest moveRequest) {

        GameResponse response = gameLogicService.makeMove(gameId, moveRequest);

        if (response.isSuccess()) {
            // Push the new state directly to all clients to avoid an extra /board fetch (and DB hit).
            simpMessagingTemplate.convertAndSend("/topic/game/" + gameId, response);
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

    @GetMapping("/{gameId}/state")
    public GameResponse getGameState(@PathVariable String gameId) {
        return gameLogicService.getGameState(gameId);
    }

    @GetMapping("/{gameId}/board")
    public Board getBoard(@PathVariable String gameId) {
        return gameLogicService.getBoard(gameId);
    }

    @PostMapping("/{gameId}/action")
    public GameResponse action(
            @PathVariable String gameId,
            @RequestBody com.group18.chessgame.dto.GameActionRequest request,
            jakarta.servlet.http.HttpSession session) {

        com.group18.chessgame.model.Player player = com.group18.chessgame.utils.SessionUtils.getCurrentPlayer(session);
        if (player == null) {
            return new GameResponse(false, "Player not authenticated", null);
        }

        GameResponse response = gameLogicService.handleAction(gameId, request.getAction(), player);
        if (response.isSuccess()) {
            simpMessagingTemplate.convertAndSend("/topic/game/" + gameId, response);
        }

        return response;
    }

    @PostMapping("/{gameId}/bot-move")
    public GameResponse botMove(
            @PathVariable String gameId,
            @RequestParam(defaultValue = "10") int depth) {
        GameResponse response = gameLogicService.makeBotMove(gameId, depth);
        if (response.isSuccess()) {
            simpMessagingTemplate.convertAndSend("/topic/game/" + gameId, response);
        }
        return response;
    }

    @GetMapping("/{gameId}/replay-moves")
    @ResponseBody
    public List<String> getReplayMoves(@PathVariable String gameId) {
        List<Move> moves = moveRepository.findByGameIdOrderByMoveOrderAsc(gameId);
        return moves.stream().map(Move::getMoveNotation).collect(Collectors.toList());
    }
}