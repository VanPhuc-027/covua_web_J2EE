package com.group18.chessgame.service;

import com.group18.chessgame.dto.GameResponse;
import com.group18.chessgame.dto.MoveRequest;
import com.group18.chessgame.enums.GameResult;
import com.group18.chessgame.enums.GameStatus;
import com.group18.chessgame.enums.GameTermination;
import com.group18.chessgame.enums.PieceColor;
import com.group18.chessgame.model.Board;
import com.group18.chessgame.model.Game;
import com.group18.chessgame.model.Player;
import com.group18.chessgame.model.Spot;
import com.group18.chessgame.model.piece.Piece;
import com.group18.chessgame.repository.GameRepository;
import com.group18.chessgame.utils.FenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class GameLogicService {

    private final GameRepository gameRepository;
    private final GameStateCache gameStateCache;
    private final GameService gameService;

    // Truyền thêm gameId vào để biết đánh trận nào
    public GameResponse makeMove(String gameId, MoveRequest move) {
        ReentrantLock lock = gameStateCache.lockFor(gameId);
        lock.lock();
        try {
            // 1. Load cached state (fast) or fallback to DB (slow).
            GameStateCache.Entry entry = gameStateCache.get(gameId)
                    .or(() -> gameStateCache.loadFromDb(gameId))
                    .orElse(null);
            if (entry == null) {
                return new GameResponse(false, "Game not found", null);
            }

            // 2. Get board + current turn from cache.
            Board board = entry.board;
            PieceColor currentTurn = entry.currentTurn;

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
            if (piece.getColor() != currentTurn) {
                return new GameResponse(false, "Not your turn", board);
            }

            // Kiểm tra luật di chuyển
            if (!piece.canMove(board, start, end)) {
                return new GameResponse(false, "Invalid move for " + piece.getName(), board);
            }

            // Không cho phép đi nước làm vua mình bị chiếu
            if (movePutsKingInCheck(board, start, end, currentTurn)) {
                return new GameResponse(false, "Move leaves king in check", board);
            }

            // 3. Thực hiện nước đi trên bàn cờ (cached)
            board.movePiece(start, end);

            // 4. Đổi lượt và kiểm tra trạng thái mới
            PieceColor nextTurn = (currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;

            boolean isCheck = isKingInCheck(board, nextTurn);
            boolean isCheckmate = isCheck && !hasAnyValidMove(board, nextTurn);
            Spot checkedKingSpot = null;
            if (isCheck) {
                checkedKingSpot = getKingSpot(board, nextTurn);
            }

            String newFen = FenUtils.boardToFen(board);

            // 5. Persist to DB with a single UPDATE (avoid extra SELECT round-trip).
            int updated = gameRepository.updateGameState(gameId, newFen, nextTurn);
            if (updated == 0) {
                return new GameResponse(false, "Game not found", null);
            }

            // 6. Update cache for fast subsequent valid-moves/board queries.
            entry.currentTurn = nextTurn;
            entry.fen = newFen;
            entry.board = board;

            GameResponse response = new GameResponse(true, "Move successful", board);
            response.setCheck(isCheck);
            response.setCheckmate(isCheckmate);
            response.setCurrentTurn(nextTurn.toString());
            if (isCheck && checkedKingSpot != null) {
                response.setKingRow(checkedKingSpot.getRow());
                response.setKingCol(checkedKingSpot.getCol());
            }
            if (isCheckmate) {
                response.setWinner(currentTurn.toString());
                // Hủy phòng - đánh dấu game kết thúc
                GameResult result = (currentTurn == PieceColor.WHITE) ? GameResult.WHITE_WINS : GameResult.BLACK_WINS;
                gameService.finishGame(gameId, result, GameTermination.CHECKMATE);
                gameStateCache.evict(gameId);
            }
            return response;
        } finally {
            lock.unlock();
        }
    }

    // Lấy nước đi hợp lệ cũng phải truyền gameId
    public List<int[]> getValidMoves(String gameId, int row, int col) {
        List<int[]> moves = new ArrayList<>();
        Optional<GameStateCache.Entry> entryOpt = gameStateCache.get(gameId)
                .or(() -> gameStateCache.loadFromDb(gameId));
        if (entryOpt.isEmpty()) return moves;

        GameStateCache.Entry entry = entryOpt.get();
        Board board = entry.board;

        Spot start = board.getSpot(row, col);
        if (start == null || start.getPiece() == null) return moves;

        Piece piece = start.getPiece();
        if (piece.getColor() != entry.currentTurn) return moves;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Spot end = board.getSpot(r, c);
                if (piece.canMove(board, start, end)) {
                    if (!movePutsKingInCheck(board, start, end, entry.currentTurn)) {
                        moves.add(new int[]{r, c});
                    }
                }
            }
        }
        return moves;
    }
    public Board getBoard(String gameId) {
        return gameStateCache.get(gameId)
                .or(() -> gameStateCache.loadFromDb(gameId))
                .map(e -> e.board)
                .orElse(null);
    }

    public boolean isKingInCheck(Board board, PieceColor color) {
        Spot kingSpot = getKingSpot(board, color);
        if (kingSpot == null) return false;

        PieceColor opponentColor = (color == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Spot spot = board.getSpot(r, c);
                Piece p = spot.getPiece();
                if (p != null && p.getColor() == opponentColor) {
                    if (p.canMove(board, spot, kingSpot)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Spot getKingSpot(Board board, PieceColor color) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Spot spot = board.getSpot(r, c);
                Piece p = spot.getPiece();
                if (p != null && p.getColor() == color && p.getName().equals("king")) {
                    return spot;
                }
            }
        }
        return null;
    }

    public boolean movePutsKingInCheck(Board board, Spot start, Spot end, PieceColor color) {
        Piece captured = end.getPiece();
        // simulate move
        board.movePiece(start, end);
        boolean inCheck = isKingInCheck(board, color);
        // undo move
        board.movePiece(end, start);
        end.setPiece(captured);
        return inCheck;
    }

    public boolean hasAnyValidMove(Board board, PieceColor color) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Spot start = board.getSpot(r, c);
                Piece p = start.getPiece();
                if (p != null && p.getColor() == color) {
                    for (int er = 0; er < 8; er++) {
                        for (int ec = 0; ec < 8; ec++) {
                            Spot end = board.getSpot(er, ec);
                            if (p.canMove(board, start, end)) {
                                if (!movePutsKingInCheck(board, start, end, color)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public GameResponse handleAction(String gameId, String action, Player player) {
        ReentrantLock lock = gameStateCache.lockFor(gameId);
        lock.lock();
        try {
            GameStateCache.Entry entry = gameStateCache.get(gameId).or(() -> gameStateCache.loadFromDb(gameId)).orElse(null);
            if (entry == null) return new GameResponse(false, "Game not found", null);

            Optional<Game> gameOpt = gameRepository.findById(gameId);
            if (gameOpt.isEmpty()) return new GameResponse(false, "Game not found", null);
            Game game = gameOpt.get();
            
            // Không xử lý nếu game đã kết thúc
            if (game.getStatus() == GameStatus.FINISHED) {
                return new GameResponse(false, "Game already finished", entry.board);
            }

            boolean isWhite = game.getWhitePlayer() != null && game.getWhitePlayer().getId() == player.getId();
            boolean isBlack = game.getBlackPlayer() != null && game.getBlackPlayer().getId() == player.getId();
            if (!isWhite && !isBlack) return new GameResponse(false, "Not a player in this game.", null);

            String colorName = isWhite ? "WHITE" : "BLACK";

            GameResponse response = new GameResponse(true, "Action processed", entry.board);
            response.setAction(action);
            response.setActionPlayer(colorName);
            
            switch (action) {
                case "RESIGN":
                    response.setWinner(isWhite ? "BLACK" : "WHITE");
                    response.setMessage("Đấu thủ " + player.getUsername() + " đã đầu hàng.");
                    gameService.finishGame(gameId,
                        isWhite ? GameResult.BLACK_WINS : GameResult.WHITE_WINS,
                        GameTermination.RESIGNATION);
                    gameStateCache.evict(gameId);
                    break;
                case "OFFER_DRAW":
                    // Chỉ thông báo, không kết thúc game
                    response.setMessage("Đấu thủ " + player.getUsername() + " muốn cầu hòa.");
                    break;
                case "ACCEPT_DRAW":
                    response.setWinner("DRAW");
                    response.setMessage("Trận đấu hòa vì hai bên đồng ý.");
                    gameService.finishGame(gameId, GameResult.DRAW, GameTermination.AGREEMENT);
                    gameStateCache.evict(gameId);
                    break;
                case "TIMEOUT":
                    response.setWinner(isWhite ? "BLACK" : "WHITE");
                    response.setMessage("Đấu thủ " + player.getUsername() + " đã hết thời gian.");
                    gameService.finishGame(gameId,
                        isWhite ? GameResult.BLACK_WINS : GameResult.WHITE_WINS,
                        GameTermination.TIMEOUT);
                    gameStateCache.evict(gameId);
                    break;
            }
            return response;
        } finally {
            lock.unlock();
        }
    }
}