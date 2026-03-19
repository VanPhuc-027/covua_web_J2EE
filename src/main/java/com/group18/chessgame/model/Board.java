package com.group18.chessgame.model;

import com.group18.chessgame.enums.PieceColor;
import com.group18.chessgame.model.piece.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Board {
    final private Spot[][] boxes = new Spot[8][8];
    private Spot enPassantTarget = null;

    public Board(){
        this.resetBoard();
    }

    public void resetBoard(){
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                boxes[i][j] = new Spot(i, j, null);
            }
        }
        for(int j=0; j<8; j++){
            boxes[1][j].setPiece(new Pawn(PieceColor.BLACK));
            boxes[6][j].setPiece(new Pawn(PieceColor.WHITE));
        }
        boxes[0][0].setPiece(new Rook(PieceColor.BLACK));
        boxes[0][7].setPiece(new Rook(PieceColor.BLACK));
        boxes[7][0].setPiece(new Rook(PieceColor.WHITE));
        boxes[7][7].setPiece(new Rook(PieceColor.WHITE));
        boxes[0][1].setPiece(new Knight(PieceColor.BLACK));
        boxes[0][6].setPiece(new Knight(PieceColor.BLACK));
        boxes[7][1].setPiece(new Knight(PieceColor.WHITE));
        boxes[7][6].setPiece(new Knight(PieceColor.WHITE));
        boxes[0][2].setPiece(new Bishop(PieceColor.BLACK));
        boxes[0][5].setPiece(new Bishop(PieceColor.BLACK));
        boxes[7][2].setPiece(new Bishop(PieceColor.WHITE));
        boxes[7][5].setPiece(new Bishop(PieceColor.WHITE));
        boxes[0][3].setPiece(new Queen(PieceColor.BLACK));
        boxes[0][4].setPiece(new King(PieceColor.BLACK));
        boxes[7][3].setPiece(new Queen(PieceColor.WHITE));
        boxes[7][4].setPiece(new King(PieceColor.WHITE));
        this.enPassantTarget = null;
    }

    public Spot getSpot(int row, int col) {
        if (!isValidPosition(row, col)) {
            return null;
        }
        return boxes[row][col];
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public void movePiece(Spot start, Spot end) {
        movePiece(start, end, true);
    }

    public void movePiece(Spot start, Spot end, boolean isRealMove) {
        Piece piece = start.getPiece();
        if (piece != null) {
            if (isRealMove) {
                // Handle En Passant capture logic
                if (piece instanceof Pawn && end == enPassantTarget) {
                    int direction = (piece.getColor() == PieceColor.WHITE) ? 1 : -1;
                    Spot capturedPawnSpot = getSpot(end.getRow() + direction, end.getCol());
                    if (capturedPawnSpot != null) {
                        capturedPawnSpot.setPiece(null);
                    }
                }
                piece.setHasMoved(true);
            }
        }
        end.setPiece(piece);
        start.setPiece(null);
    }
}
