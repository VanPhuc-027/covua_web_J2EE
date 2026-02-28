package com.group18.chessgame.model.piece;

import com.group18.chessgame.enums.PieceColor;
import com.group18.chessgame.model.Board;
import com.group18.chessgame.model.Spot;

public class Queen extends Piece {
    public Queen(PieceColor color) {
        super(color, "queen");
    }

    @Override
    public boolean canMove(Board board, Spot start, Spot end) {
        // Implement queen movement logic here
        return false; // Placeholder return value
    }
}
