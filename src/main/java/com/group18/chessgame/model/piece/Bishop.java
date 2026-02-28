package com.group18.chessgame.model.piece;

import com.group18.chessgame.enums.PieceColor;
import com.group18.chessgame.model.Board;
import com.group18.chessgame.model.Spot;

public class Bishop extends Piece {
    public Bishop(PieceColor color) {
        super(color, "bishop");
    }

    @Override
    public boolean canMove(Board board, Spot start, Spot end) {
        // Implement bishop movement logic here
        return false; // Placeholder return value
    }
}
