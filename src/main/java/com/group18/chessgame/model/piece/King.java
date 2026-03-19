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

        if (rowDiff <= 1 && colDiff <= 1) {
            return true;
        }

        // Castle move
        if (rowDiff == 0 && colDiff == 2 && !this.isHasMoved()) {
            int row = start.getRow();
            // Short castle
            if (end.getCol() == 6) {
                Spot rookSpot = board.getSpot(row, 7);
                Piece rook = rookSpot != null ? rookSpot.getPiece() : null;
                if (rook != null && rook.getName().equals("rook") && !rook.isHasMoved()) {
                    return board.getSpot(row, 5).getPiece() == null && board.getSpot(row, 6).getPiece() == null;
                }
            }
            // Long castle
            if (end.getCol() == 2) {
                Spot rookSpot = board.getSpot(row, 0);
                Piece rook = rookSpot != null ? rookSpot.getPiece() : null;
                if (rook != null && rook.getName().equals("rook") && !rook.isHasMoved()) {
                    return board.getSpot(row, 1).getPiece() == null &&
                           board.getSpot(row, 2).getPiece() == null &&
                           board.getSpot(row, 3).getPiece() == null;
                }
            }
        }

        return false;
    }
}
