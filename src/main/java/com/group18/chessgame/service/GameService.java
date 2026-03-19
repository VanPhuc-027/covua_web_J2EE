package com.group18.chessgame.service;

import com.group18.chessgame.enums.GameMode;
import com.group18.chessgame.enums.GameResult;
import com.group18.chessgame.enums.GameStatus;
import com.group18.chessgame.enums.GameTermination;
import com.group18.chessgame.model.Board;
import com.group18.chessgame.model.Game;
import com.group18.chessgame.model.Player;
import com.group18.chessgame.repository.GameRepository;
import com.group18.chessgame.repository.PlayerRepository;
import com.group18.chessgame.utils.FenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.group18.chessgame.dto.GameHistoryDTO;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public Game createGame(Player creator, GameMode gameMode) {
        Game game = new Game (
                creator,
                null,
                gameMode
        );
        game.setCurrentFen(FenUtils.boardToFen(new Board()));
        Game savedGame = gameRepository.save(game);
        messagingTemplate.convertAndSend("/topic/lobby", "RELOAD_LOBBY:" + creator.getUsername());
        return savedGame;
    }

    public Game joinGame(String gameId, Player player) {
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) return null;

        boolean isWhite = game.getWhitePlayer() != null && game.getWhitePlayer().getUsername().equals(player.getUsername());
        boolean isBlack = game.getBlackPlayer() != null && game.getBlackPlayer().getUsername().equals(player.getUsername());
        if (isWhite || isBlack) {
            return game;
        }

        if (game.getStatus() == GameStatus.WAITING) {
            if (game.getWhitePlayer() == null) {
                game.setWhitePlayer(player);
            } else {
                game.setBlackPlayer(player);
            }
            game.setStatus(GameStatus.IN_PROGRESS);
            game.setStartedAt(LocalDateTime.now());
            Game savedGame = gameRepository.save(game);
            messagingTemplate.convertAndSend("/topic/lobby", "RELOAD_LOBBY:" + player.getUsername());
            return savedGame;
        }
        return null;
    }

    public Game finishGame(String gameId, GameResult gameResult, GameTermination termination) {
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) return null;
        game.setStatus(GameStatus.FINISHED);
        game.setResult(gameResult);
        game.setTermination(termination);
        game.setFinishedAt(LocalDateTime.now());
        calculateEloChanges(game);
        Game savedGame = gameRepository.save(game);
        messagingTemplate.convertAndSend("/topic/lobby", "RELOAD_LOBBY:system");
        return savedGame;
    }

    private void calculateEloChanges(Game game) {
        Player whitePlayer = game.getWhitePlayer();
        Player blackPlayer = game.getBlackPlayer();

        if (whitePlayer == null || blackPlayer == null) return;

        double expectedWhite = 1.0 / (1 + Math.pow(10, (blackPlayer.getEloRating() - whitePlayer.getEloRating()) / 400.0));
        double expectedBlack = 1.0 - expectedWhite;

        int k = 32;
        double scoreWhite, scoreBlack;

        switch (game.getResult()) {
            case WHITE_WINS -> {
                scoreWhite = 1.0;
                scoreBlack = 0.0;
            }
            case BLACK_WINS -> {
                scoreWhite = 0.0;
                scoreBlack = 1.0;
            }
            case null, default -> {
                scoreWhite = 0.5;
                scoreBlack = 0.5;
            }
        }

        int whiteEloChange = (int) Math.round(k * (scoreWhite - expectedWhite));
        int blackEloChange = (int) Math.round(k * (scoreBlack - expectedBlack));
        game.setWhiteEloChange(whiteEloChange);
        game.setBlackEloChange(blackEloChange);
        whitePlayer.setEloRating(whitePlayer.getEloRating() + whiteEloChange);
        blackPlayer.setEloRating(blackPlayer.getEloRating() + blackEloChange);
        playerRepository.save(whitePlayer);
        playerRepository.save(blackPlayer);
    }
    public Game getGame(String gameId) {
        return gameRepository.findById(gameId).orElse(null);
    }

    public List<Game> getWaitingGame() {
        return gameRepository.findByStatusIn(Arrays.asList(GameStatus.WAITING, GameStatus.IN_PROGRESS));
    }

    public Page<GameHistoryDTO> getGameHistory(long playerId, Pageable pageable) {
        Page<Game> gamesPage = gameRepository.findGameHistory(playerId, pageable);
        List<GameHistoryDTO> dtos = gamesPage.getContent().stream().map(game -> {
            boolean isWhite = game.getWhitePlayer().getId() == playerId;
            String opponentName = isWhite ? (game.getBlackPlayer() != null ? game.getBlackPlayer().getUsername() : "Unknown")
                                         : (game.getWhitePlayer() != null ? game.getWhitePlayer().getUsername() : "Unknown");
            int eloChange = isWhite ? game.getWhiteEloChange() : game.getBlackEloChange();

            String outcome;
            if (game.getResult() == GameResult.DRAW) {
                outcome = "Hòa";
            } else if ((isWhite && game.getResult() == GameResult.WHITE_WINS) ||
                       (!isWhite && game.getResult() == GameResult.BLACK_WINS)) {
                outcome = "Thắng";
            } else {
                outcome = "Thua";
            }

            return GameHistoryDTO.builder()
                    .gameId(game.getId())
                    .startedAt(game.getStartedAt())
                    .finishedAt(game.getFinishedAt())
                    .result(outcome)
                    .opponentName(opponentName)
                    .eloChange(eloChange)
                    .build();
        }).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, gamesPage.getTotalElements());
    }

    public long getTotalWins(long playerId) {
        return gameRepository.countWins(playerId);
    }

    public long getTotalLosses(long playerId) {
        return gameRepository.countLosses(playerId);
    }

    public void removeGame(String gameId) {
        gameRepository.deleteById(gameId);
        messagingTemplate.convertAndSend("/topic/lobby", "RELOAD_LOBBY:system");
    }
}
