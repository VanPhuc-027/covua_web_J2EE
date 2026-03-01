package com.group18.chessgame.model.piece;

import com.group18.chessgame.enums.PieceColor;
import com.group18.chessgame.model.Board;
import com.group18.chessgame.model.Spot;

public class King extends Piece{
    public King(PieceColor color) {
        super(color, "king");
    }

    @Override
    public boolean canMove(Board board, Spot start, Spot end) {
        // Implement king movement logic here
        return false; // Placeholder return value
    }
}
