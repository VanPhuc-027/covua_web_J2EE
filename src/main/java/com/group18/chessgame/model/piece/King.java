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

        if (end.getPiece() != null &&
                end.getPiece().getColor() == this.getColor()) {
            return false;
        }

        int rowDiff = Math.abs(start.getRow() - end.getRow());
        int colDiff = Math.abs(start.getCol() - end.getCol());

        return rowDiff <= 1 && colDiff <= 1;
    }
}
