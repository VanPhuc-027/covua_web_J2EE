package com.group18.chessgame.model;

import com.group18.chessgame.enums.GameMode;
import com.group18.chessgame.enums.GameResult;
import com.group18.chessgame.enums.GameStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class Game {
    private String id;
    private Player whitePlayer;
    private Player blackPlayer;

    private GameMode gameMode;
    private GameStatus status;
    private GameResult result;

    private List<String> moveHistory = new ArrayList<>();

    private int whiteEloChange = 0;
    private int blackEloChange = 0;

    private int timeLimitSeconds = 600;
    private int whiteTimeLeft;
    private int blackTimeLeft;

    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    public Game(Player whitePlayer, Player blackPlayer, GameMode gameMode) {
        this.id = UUID.randomUUID().toString();
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.gameMode = gameMode;
        this.status = GameStatus.WAITING;
        this.result = null;
        this.whiteTimeLeft = timeLimitSeconds;
        this.blackTimeLeft = timeLimitSeconds;
        this.createdAt = LocalDateTime.now();
    }
}
