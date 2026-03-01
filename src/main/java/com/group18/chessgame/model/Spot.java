package com.group18.chessgame.model;

import com.group18.chessgame.model.piece.Piece;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Spot {
    private int x;
    private int y;
    private Piece piece;
}
