package com.group18.chessgame.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Move {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    private String moveNotation;
    private String fenAfterMove;
    private String uciMove;   // định dạng "e2e4" – lưu ô xuất phát + ô đích
    private int moveOrder;
    private LocalDateTime timestamp = LocalDateTime.now();
}
