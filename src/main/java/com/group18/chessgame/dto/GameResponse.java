package com.group18.chessgame.dto;

import com.group18.chessgame.model.Board;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameResponse {

    private boolean success;
    private String message;
    private Board board;

}