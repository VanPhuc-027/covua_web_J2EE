package com.group18.chessgame.model.piece;

import com.group18.chessgame.enums.PieceColor;
import com.group18.chessgame.model.Board;
import com.group18.chessgame.model.Spot;

public class Knight extends Piece{
    public Knight(PieceColor color) {
        super(color, "knight");
    }

    @Override
    public boolean canMove(Board board, Spot start, Spot end) {
        // Implement knight movement logic here
        return false; // Placeholder return value
    }
}
