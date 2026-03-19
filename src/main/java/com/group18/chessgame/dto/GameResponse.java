package com.group18.chessgame.dto;

import com.group18.chessgame.model.Board;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameResponse {

    private boolean success;
    private String message;
    private Board board;
    private boolean check;
    private boolean checkmate;
    private String winner;
    private Integer kingRow;
    private Integer kingCol;
    
    private String action;
    private String actionPlayer;
    private String currentTurn;

    public GameResponse(boolean success, String message, Board board) {
        this.success = success;
        this.message = message;
        this.board = board;
    }

}