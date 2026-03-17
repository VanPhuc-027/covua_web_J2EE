package com.group18.chessgame.service;

import com.group18.chessgame.dto.GameResponse;
import com.group18.chessgame.dto.MoveRequest;
import com.group18.chessgame.enums.PieceColor;
import com.group18.chessgame.model.Board;
import com.group18.chessgame.model.Game;
import com.group18.chessgame.model.Spot;
import com.group18.chessgame.model.piece.Piece;
import com.group18.chessgame.repository.GameRepository;
import com.group18.chessgame.utils.FenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameLogicService {

    private final GameRepository gameRepository;

    // Truyền thêm gameId vào để biết đánh trận nào
    public GameResponse makeMove(String gameId, MoveRequest move) {
        // 1. Lấy dữ liệu trận đấu từ MySQL
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {
            return new GameResponse(false, "Game not found", null);
        }

        // 2. Tái tạo bàn cờ từ chuỗi FEN
        Board board = new Board();
        FenUtils.fenToBoard(game.getCurrentFen(), board);

        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow = move.getToRow();
        int toCol = move.getToCol();

        Spot start = board.getSpot(fromRow, fromCol);
        Spot end = board.getSpot(toRow, toCol);

        if (start == null || end == null) return new GameResponse(false, "Invalid position", board);

        Piece piece = start.getPiece();
        if (piece == null) return new GameResponse(false, "No piece at start position", board);

        // Kiểm tra đúng lượt đi không
        if (piece.getColor() != game.getCurrentTurn()) {
            return new GameResponse(false, "Not your turn", board);
        }

        // Kiểm tra luật di chuyển
        if (!piece.canMove(board, start, end)) {
            return new GameResponse(false, "Invalid move for " + piece.getName(), board);
        }

        // 3. Thực hiện nước đi trên bàn cờ ảo
        board.movePiece(start, end);

        // 4. Đổi lượt và cập nhật lại chuỗi FEN mới
        game.setCurrentTurn(game.getCurrentTurn() == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE);
        game.setCurrentFen(FenUtils.boardToFen(board));

        // 5. Lưu xuống DB
        gameRepository.save(game);

        return new GameResponse(true, "Move successful", board);
    }

    // Lấy nước đi hợp lệ cũng phải truyền gameId
    public List<int[]> getValidMoves(String gameId, int row, int col) {
        List<int[]> moves = new ArrayList<>();
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) return moves;

        Board board = new Board();
        FenUtils.fenToBoard(game.getCurrentFen(), board);

        Spot start = board.getSpot(row, col);
        if (start == null || start.getPiece() == null) return moves;

        Piece piece = start.getPiece();
        if (piece.getColor() != game.getCurrentTurn()) return moves;

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
    public Board getBoard(String gameId) {
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) return null;

        Board board = new Board();
        FenUtils.fenToBoard(game.getCurrentFen(), board);
        return board;
    }
}