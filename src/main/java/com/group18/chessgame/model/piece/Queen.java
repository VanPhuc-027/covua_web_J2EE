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

        if (end.getPiece() != null &&
                end.getPiece().getColor() == this.getColor()) {
            return false;
        }

        int startRow = start.getRow();
        int startCol = start.getCol();
        int endRow = end.getRow();
        int endCol = end.getCol();

        int rowDiff = Math.abs(startRow - endRow);
        int colDiff = Math.abs(startCol - endCol);

        // Diagonal
        if (rowDiff == colDiff) {

            int rowStep = (endRow > startRow) ? 1 : -1;
            int colStep = (endCol > startCol) ? 1 : -1;

            int row = startRow + rowStep;
            int col = startCol + colStep;

            while (row != endRow && col != endCol) {

                if (board.getSpot(row, col).getPiece() != null) {
                    return false;
                }

                row += rowStep;
                col += colStep;
            }

            return true;
        }

        // Straight
        if (startRow == endRow || startCol == endCol) {

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
        return false;
    }
}
