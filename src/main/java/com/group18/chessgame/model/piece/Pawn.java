package com.group18.chessgame.model.piece;

import com.group18.chessgame.enums.PieceColor;
import com.group18.chessgame.model.Board;
import com.group18.chessgame.model.Spot;

public class Pawn extends Piece{
    public Pawn(PieceColor color) {
        super(color, "pawn");
    }

    @Override
    public boolean canMove(Board board, Spot start, Spot end) {
        // Implement pawn movement logic here
        return false; // Placeholder return value
    }
}
