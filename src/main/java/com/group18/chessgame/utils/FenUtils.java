package com.group18.chessgame.utils;

import com.group18.chessgame.enums.PieceColor;
import com.group18.chessgame.model.Board;
import com.group18.chessgame.model.Spot;
import com.group18.chessgame.model.piece.*;

public class FenUtils {

    public static String boardToFen(Board board, PieceColor currentTurn) {
        StringBuilder fen = new StringBuilder();

        for (int r = 0; r < 8; r++) {
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

        fen.append(" ").append(currentTurn == PieceColor.WHITE ? "w" : "b");

        fen.append(" ").append(getCastlingRights(board));

        fen.append(" ").append(getEnPassantTarget(board));

        fen.append(" 0");
        fen.append(" 1");

        return fen.toString();
    }

    private static String getCastlingRights(Board board) {
        StringBuilder rights = new StringBuilder();

        Spot whiteKingSpot = board.getSpot(7, 4);
        Piece whiteKing = whiteKingSpot != null ? whiteKingSpot.getPiece() : null;
        if (whiteKing instanceof King && !whiteKing.isHasMoved()) {
            Spot whiteRookK = board.getSpot(7, 7);
            Piece rookK = whiteRookK != null ? whiteRookK.getPiece() : null;
            if (rookK instanceof Rook && !rookK.isHasMoved())
                rights.append("K");

            Spot whiteRookQ = board.getSpot(7, 0);
            Piece rookQ = whiteRookQ != null ? whiteRookQ.getPiece() : null;
            if (rookQ instanceof Rook && !rookQ.isHasMoved())
                rights.append("Q");
        }

        Spot blackKingSpot = board.getSpot(0, 4);
        Piece blackKing = blackKingSpot != null ? blackKingSpot.getPiece() : null;
        if (blackKing instanceof King && !blackKing.isHasMoved()) {
            Spot blackRookK = board.getSpot(0, 7);
            Piece rookK = blackRookK != null ? blackRookK.getPiece() : null;
            if (rookK instanceof Rook && !rookK.isHasMoved())
                rights.append("k");

            Spot blackRookQ = board.getSpot(0, 0);
            Piece rookQ = blackRookQ != null ? blackRookQ.getPiece() : null;
            if (rookQ instanceof Rook && !rookQ.isHasMoved())
                rights.append("q");
        }

        return rights.length() == 0 ? "-" : rights.toString();
    }

    private static String getEnPassantTarget(Board board) {
        Spot target = board.getEnPassantTarget();
        if (target == null)
            return "-";
        return (char) ('a' + target.getCol()) + String.valueOf(8 - target.getRow());
    }

    public static String boardToFen(Board board) {
        return boardToFen(board, PieceColor.WHITE);
    }

    public static void fenToBoard(String fen, Board board) {
        if (fen == null || fen.isEmpty())
            return;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board.getSpot(r, c) != null) {
                    board.getSpot(r, c).setPiece(null);
                }
            }
        }

        String[] parts = fen.split(" ");
        String placement = parts[0];
        String[] rows = placement.split("/");

        for (int r = 0; r < 8; r++) {
            String rowString = rows[r];
            int c = 0;
            for (char ch : rowString.toCharArray()) {
                if (Character.isDigit(ch)) {
                    c += Character.getNumericValue(ch);
                } else {
                    Piece piece = createPieceFromChar(ch);
                    if (piece != null) {
                        piece.setHasMoved(true);
                        board.getSpot(r, c).setPiece(piece);
                    }
                    c++;
                }
            }
        }

        if (parts.length > 2) {
            String rights = parts[2];
            if (rights.contains("K"))
                resetHasMoved(board, 7, 4, 7, 7);
            if (rights.contains("Q"))
                resetHasMoved(board, 7, 4, 7, 0);
            if (rights.contains("k"))
                resetHasMoved(board, 0, 4, 0, 7);
            if (rights.contains("q"))
                resetHasMoved(board, 0, 4, 0, 0);
        }

        board.setEnPassantTarget(null);
        if (parts.length > 3 && !parts[3].equals("-")) {
            String ep = parts[3];
            int col = ep.charAt(0) - 'a';
            int row = 8 - Character.getNumericValue(ep.charAt(1));
            board.setEnPassantTarget(board.getSpot(row, col));
        }
    }

    private static void resetHasMoved(Board board, int kR, int kC, int rR, int rC) {
        Spot kS = board.getSpot(kR, kC);
        Spot rS = board.getSpot(rR, rC);
        if (kS != null && kS.getPiece() instanceof King)
            kS.getPiece().setHasMoved(false);
        if (rS != null && rS.getPiece() instanceof Rook)
            rS.getPiece().setHasMoved(false);
    }

    private static char getPieceChar(Piece piece) {
        char c;
        String name = piece.getName();
        switch (name.toLowerCase()) {
            case "pawn" -> c = 'p';
            case "knight" -> c = 'n';
            case "bishop" -> c = 'b';
            case "rook" -> c = 'r';
            case "queen" -> c = 'q';
            case "king" -> c = 'k';
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
