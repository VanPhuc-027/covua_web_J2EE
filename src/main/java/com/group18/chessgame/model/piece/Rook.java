package com.group18.chessgame.model.piece;

import com.group18.chessgame.enums.PieceColor;
import com.group18.chessgame.model.Board;
import com.group18.chessgame.model.Spot;

public class Rook extends Piece {

    public Rook(PieceColor color) {
        super(color, "rook");
    }

    @Override
    public boolean canMove(Board board, Spot start, Spot end) {

        if (end.getPiece() != null &&
                end.getPiece().getColor() == this.getColor()) {
            return false;
        }

        int startRow = start.getRow();
        int startCol = start.getCol();
        int endRow = end.getRow();
        int endCol = end.getCol();

        if (startRow != endRow && startCol != endCol) {
            return false;
        }

        int rowStep = Integer.compare(endRow, startRow);
        int colStep = Integer.compare(endCol, startCol);

        int row = startRow + rowStep;
        int col = startCol + colStep;

        while (row != endRow || col != endCol) {
            if (board.getSpot(row, col).getPiece() != null) {
                return false;
            }
            row += rowStep;
            col += colStep;
        }
        return true;
    }
}
