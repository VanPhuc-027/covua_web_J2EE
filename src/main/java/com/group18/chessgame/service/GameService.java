package com.group18.chessgame.service;

import com.group18.chessgame.enums.GameMode;
import com.group18.chessgame.enums.GameResult;
import com.group18.chessgame.enums.GameStatus;
import com.group18.chessgame.enums.GameTermination;
import com.group18.chessgame.model.Game;
import com.group18.chessgame.model.Player;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {
    private final Map<String, Game> activeGames = new ConcurrentHashMap<>();

    public Game createGame(Player creator, GameMode gameMode) {
        boolean isWhite = Math.random() < 0.5;

        Game game = new Game (
                isWhite ? creator : null,
                isWhite ? null : creator,
                gameMode
        );
        activeGames.put(game.getId(), game);
        return game;
    }

    public Game joinGame(String gameId, Player player) {
        Game game = activeGames.get(gameId);
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
            return game;
        }
        return null;
    }

    public Game finishGame(String gameId, GameResult gameResult, GameTermination termination) {
        Game game = activeGames.get(gameId);
        if (game == null) return null;
        game.setStatus(GameStatus.FINISHED);
        game.setResult(gameResult);
        game.setTermination(termination);
        game.setFinishedAt(LocalDateTime.now());
        calculateEloChanges(game);
        return game;
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
    }
    public Game getGame(String gameId) {
        return activeGames.get(gameId);
    }

    public List<Game> getWaitingGame() {
        return activeGames.values().stream()
                .filter(game -> game.getStatus() == GameStatus.WAITING)
                .toList();
    }

    public void removeGame(String gameId) {
        activeGames.remove(gameId);
    }
}
