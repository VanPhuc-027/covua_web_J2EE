package com.group18.chessgame.utils;

import com.group18.chessgame.enums.PieceColor;
import com.group18.chessgame.model.Board;
import com.group18.chessgame.model.Spot;
import com.group18.chessgame.model.piece.*;

public class FenUtils {
    public static String boardToFen(Board board) {
        StringBuilder fen = new StringBuilder();

        for(int r = 0; r < 8; r++) {
            int emptyCount = 0;
            for (int c = 0; c < 8; c++) {
                Spot spot = board.getSpot(r, c);
                if (spot != null && spot.getPiece() != null) {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    fen.append(getPieceChar(spot.getPiece()));
                } else {
                    emptyCount++;
                }
            }
            if (emptyCount > 0) {
                fen.append(emptyCount);
            }
            if (r < 7) {
                fen.append('/');
            }
        }
        return fen.toString();
    }

    public static void fenToBoard(String fen, Board board) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board.getSpot(r, c) != null) {
                    board.getSpot(r, c).setPiece(null);
                }
            }
        }
        String placement = fen.split(" ")[0];
        String[] rows = placement.split("/");
        for (int r = 0; r < 8; r++) {
            String rowString = rows[r];
            int c = 0;
            for (char ch : rowString.toCharArray()) {
                if (Character.isDigit(ch)) {
                    c += Character.getNumericValue(ch);
                } else {
                    Piece piece = createPieceFromChar(ch);
                    board.getSpot(r, c).setPiece(piece);
                    c++;
                }
            }
        }
    }

    private static char getPieceChar(Piece piece) {
        char c;
        String name = piece.getClass().getSimpleName();
        switch (name) {
            case "Pawn" -> c = 'p';
            case "Knight" -> c = 'n';
            case "Bishop" -> c = 'b';
            case "Rook" -> c = 'r';
            case "Queen" -> c = 'q';
            case "King" -> c = 'k';
            default -> c = '?';
        }
        if (piece.getColor() == PieceColor.WHITE) {
            return Character.toUpperCase(c);
        }
        return c;
    }

    private static Piece createPieceFromChar(char ch) {
        PieceColor color = Character.isUpperCase(ch) ? PieceColor.WHITE : PieceColor.BLACK;
        char lower = Character.toLowerCase(ch);
        return switch (lower) {
            case 'p' -> new Pawn(color);
            case 'r' -> new Rook(color);
            case 'n' -> new Knight(color);
            case 'b' -> new Bishop(color);
            case 'q' -> new Queen(color);
            case 'k' -> new King(color);
            default -> null;
        };
    }
}
