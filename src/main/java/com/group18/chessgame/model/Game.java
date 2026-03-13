package com.group18.chessgame.model;

import com.group18.chessgame.enums.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "games")
@Data
@NoArgsConstructor
public class Game {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "white_player_id")
    private Player whitePlayer;

    @ManyToOne
    @JoinColumn(name = "black_player_id")
    private Player blackPlayer;

    @Enumerated(EnumType.STRING)
    private GameMode gameMode;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @Enumerated(EnumType.STRING)
    private GameResult result;

    @Enumerated(EnumType.STRING)
    private GameTermination termination;

    @Transient
    private Board board = new Board();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<Move> moveHistory = new ArrayList<>();

    @Column(columnDefinition = "VARCHAR(255) DEFAULT 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR'")
    private String currentFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";

    @Enumerated(EnumType.STRING)
    private PieceColor currentTurn = PieceColor.WHITE;

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
        this.board = new Board();
    }
}