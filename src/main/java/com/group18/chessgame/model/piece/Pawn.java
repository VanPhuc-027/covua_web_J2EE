package com.group18.chessgame.model.piece;

import com.group18.chessgame.enums.PieceColor;
import com.group18.chessgame.model.Board;
import com.group18.chessgame.model.Spot;

public class Pawn extends Piece {

    public Pawn(PieceColor color) {
        super(color, "pawn");
    }

    @Override
    public boolean canMove(Board board, Spot start, Spot end) {

        int direction = (getColor() == PieceColor.WHITE) ? -1 : 1;

        int startRow = start.getRow();
        int startCol = start.getCol();
        int endRow = end.getRow();
        int endCol = end.getCol();

        // Không ăn quân cùng màu
        if (end.getPiece() != null &&
                end.getPiece().getColor() == this.getColor()) {
            return false;
        }

        // đi thẳng 1 ô
        if (startCol == endCol && endRow == startRow + direction && end.getPiece() == null) {
            return true;
        }

        // đi 2 ô từ vị trí ban đầu
        if (startCol == endCol &&
                ((getColor() == PieceColor.WHITE && startRow == 6) ||
                        (getColor() == PieceColor.BLACK && startRow == 1)) &&
                endRow == startRow + 2 * direction &&
                board.getSpot(startRow + direction, startCol).getPiece() == null &&
                end.getPiece() == null) {
            return true;
        }

        // ăn chéo
        if (Math.abs(startCol - endCol) == 1 &&
                endRow == startRow + direction) {
            // Ăn quân bình thường
            if (end.getPiece() != null) {
                return true;
            }
            // Bắt tốt qua đường (En Passant)
            if (end == board.getEnPassantTarget()) {
                return true;
            }
        }

        return false;
    }
}