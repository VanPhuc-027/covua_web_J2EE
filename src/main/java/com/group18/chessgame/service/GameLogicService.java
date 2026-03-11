package com.group18.chessgame.service;

import com.group18.chessgame.dto.GameResponse;
import com.group18.chessgame.dto.MoveRequest;
import com.group18.chessgame.enums.PieceColor;
import com.group18.chessgame.model.Board;
import com.group18.chessgame.model.Spot;
import com.group18.chessgame.model.piece.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameLogicService {

    private Board board;
    private PieceColor currentTurn;

    public GameLogicService() {
        this.board = new Board();
        this.currentTurn = PieceColor.WHITE;
    }

    public GameResponse makeMove(MoveRequest move) {

        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow = move.getToRow();
        int toCol = move.getToCol();

        Spot start = board.getSpot(fromRow, fromCol);
        Spot end = board.getSpot(toRow, toCol);

        if (start == null || end == null) {
            return new GameResponse(false, "Invalid position", board);
        }

        Piece piece = start.getPiece();

        if (piece == null) {
            return new GameResponse(false, "No piece at start position", board);
        }

        // kiểm tra đúng lượt
        if (piece.getColor() != currentTurn) {
            return new GameResponse(false, "Not your turn", board);
        }

        // kiểm tra luật di chuyển
        if (!piece.canMove(board, start, end)) {
            return new GameResponse(false, "Invalid move for " + piece.getName(), board);
        }
        // di chuyển quân
        board.movePiece(start, end);
        //phong quân
        handlePromotion(end, move.getPromotion());
        // đổi lượt
        switchTurn();
        return new GameResponse(true, "Move successful", board);
    }

    private void switchTurn() {
        currentTurn = (currentTurn == PieceColor.WHITE)
                ? PieceColor.BLACK
                : PieceColor.WHITE;
    }

    public Board getBoard() {
        return board;
    }

    public void resetGame() {
        board.resetBoard();
        currentTurn = PieceColor.WHITE;
    }

    public PieceColor getCurrentTurn() {
        return currentTurn;
    }

    public List<int[]> getValidMoves(int row, int col) {

        List<int[]> moves = new ArrayList<>();

        Spot start = board.getSpot(row, col);

        if (start == null || start.getPiece() == null)
            return moves;

        Piece piece = start.getPiece();
        if (piece.getColor() != currentTurn)
            return moves;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {

                Spot end = board.getSpot(r, c);

                if (piece.canMove(board, start, end)) {
                    moves.add(new int[]{r, c});
                }
            }
        }

        return moves;
    }

    private void handlePromotion(Spot end, String promotion) {

        Piece piece = end.getPiece();

        if (!(piece instanceof Pawn)) return;

        PieceColor color = piece.getColor();
        int row = end.getRow();

        boolean isPromotion =
                (color == PieceColor.WHITE && row == 0) ||
                        (color == PieceColor.BLACK && row == 7);

        if (!isPromotion) return;

        if (promotion == null || promotion.isBlank()) {
            promotion = "QUEEN";
        }

        promotion = promotion.toUpperCase();

        Piece newPiece;

        switch (promotion) {
            case "ROOK":
                newPiece = new Rook(color);
                break;
            case "BISHOP":
                newPiece = new Bishop(color);
                break;
            case "KNIGHT":
                newPiece = new Knight(color);
                break;
            default:
                newPiece = new Queen(color);
        }

        end.setPiece(newPiece);
    }

}