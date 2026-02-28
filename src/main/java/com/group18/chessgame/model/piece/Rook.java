package com.group18.chessgame.model.piece;

import com.group18.chessgame.enums.PieceColor;
import com.group18.chessgame.model.Board;
import com.group18.chessgame.model.Spot;

public class Rook extends Piece{
    public Rook(PieceColor color) {
        super(color, "rook");
    }

    @Override
    public boolean canMove(Board board, Spot start, Spot end) {
        // Implement rook movement logic here
        return false; // Placeholder return value
    }
}
