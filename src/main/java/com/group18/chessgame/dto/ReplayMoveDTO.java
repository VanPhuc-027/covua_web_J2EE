package com.group18.chessgame.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReplayMoveDTO {
    private String notation;
    private String fen;
    private String uciMove;  // "e2e4" để highlight ô from/to
}
