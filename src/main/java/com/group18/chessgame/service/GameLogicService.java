package com.group18.chessgame.service;

import com.group18.chessgame.dto.GameResponse;
import com.group18.chessgame.dto.MoveRequest;
import com.group18.chessgame.enums.GameResult;
import com.group18.chessgame.enums.GameStatus;
import com.group18.chessgame.enums.GameTermination;
import com.group18.chessgame.enums.PieceColor;
import com.group18.chessgame.model.Board;
import com.group18.chessgame.model.Game;
import com.group18.chessgame.model.Move;
import com.group18.chessgame.model.Player;
import com.group18.chessgame.model.Spot;
import com.group18.chessgame.model.piece.*;
import com.group18.chessgame.repository.GameRepository;
import com.group18.chessgame.repository.MoveRepository;
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
    private final GameLobbyService gameLobbyService;
    private final MoveRepository moveRepository;
    private final StockfishApiService stockfishApiService;

    private String getAlgebraic(int row, int col) {
        return "" + (char) ('a' + col) + (8 - row);
    }

    private String getNotation(Piece piece, int fromRow, int fromCol, int toRow, int toCol, boolean isCapture) {
        String pName = piece.getName();
        String prefix = switch (pName) {
            case "pawn" -> "";
            case "knight" -> "N";
            case "bishop" -> "B";
            case "rook" -> "R";
            case "queen" -> "Q";
            case "king" -> "K";
            default -> "";
        };
        String capture = isCapture ? "x" : "";
        return prefix + capture + getAlgebraic(toRow, toCol);
    }

    public GameResponse makeMove(String gameId, MoveRequest move) {
        ReentrantLock lock = gameStateCache.lockFor(gameId);
        lock.lock();
        try {
            GameStateCache.Entry entry = gameStateCache.get(gameId)
                    .or(() -> gameStateCache.loadFromDb(gameId))
                    .orElse(null);
            if (entry == null) return new GameResponse(false, "Game not found", null);

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
            if (piece.getColor() != currentTurn) return new GameResponse(false, "Not your turn", board);

            if (!piece.canMove(board, start, end)) {
                return new GameResponse(false, "Invalid move for " + piece.getName(), board);
            }

            if (movePutsKingInCheck(board, start, end, currentTurn)) {
                return new GameResponse(false, "Move leaves king in check", board);
            }

            // Record notation BEFORE moving (to check if it was a capture)
            boolean isCapture = end.getPiece() != null;
            String notation = getNotation(piece, fromRow, fromCol, toRow, toCol, isCapture);

            // Xử lý nhập thành
            if (piece instanceof King && Math.abs(fromCol - toCol) == 2) {
                int rookFromCol = (toCol > fromCol) ? 7 : 0;
                int rookToCol = (toCol > fromCol) ? 5 : 3;
                board.movePiece(board.getSpot(fromRow, rookFromCol), board.getSpot(fromRow, rookToCol));
                notation = (toCol > fromCol) ? "O-O" : "O-O-O";
            }

            board.movePiece(start, end);
            
            // Xử lý phong quân
            if (piece instanceof Pawn && ((currentTurn == PieceColor.WHITE && toRow == 0) || (currentTurn == PieceColor.BLACK && toRow == 7))) {
                String promo = move.getPromotion() != null ? move.getPromotion().toLowerCase() : "queen";
                Piece newPiece = switch (promo) {
                    case "rook" -> new Rook(currentTurn);
                    case "bishop" -> new Bishop(currentTurn);
                    case "knight" -> new Knight(currentTurn);
                    default -> new Queen(currentTurn);
                };
                end.setPiece(newPiece);
                notation += "=" + (promo.equals("knight") ? "N" : promo.substring(0, 1).toUpperCase());
            }

            PieceColor nextTurn = (currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
            String newFen = FenUtils.boardToFen(board);

            entry.currentTurn = nextTurn;
            entry.fen = newFen;
            entry.moveHistory.add(notation);

            // Persist Move asynchronously - do NOT block the move response with a DB round-trip
            final String savedGameId = gameId;
            final String savedNotation = notation;
            final int savedOrder = entry.moveHistory.size();
            final String savedFen = newFen;
            final PieceColor savedTurn = nextTurn;
            new Thread(() -> {
                try {
                    // Update main game table
                    gameRepository.updateGameState(savedGameId, savedFen, savedTurn);
                    
                    // Save individual move history
                    Move moveEntity = new Move();
                    // Avoid loading the entire Game entity; just set the FK via a proxy
                    Game gameRef = gameRepository.getReferenceById(savedGameId);
                    moveEntity.setGame(gameRef);
                    moveEntity.setMoveNotation(savedNotation);
                    moveEntity.setMoveOrder(savedOrder);
                    moveRepository.save(moveEntity);
                } catch (Exception ex) { /* log */ }
            }).start();

            boolean isCheck = isKingInCheck(board, nextTurn);
            boolean isCheckmate = isCheck && !hasAnyValidMove(board, nextTurn);
            
            GameResponse response = new GameResponse(true, "Move successful", board);
            response.setCheck(isCheck);
            response.setCheckmate(isCheckmate);
            response.setCurrentTurn(nextTurn.toString());
            response.setMoveHistory(new ArrayList<>(entry.moveHistory));
            
            response.setValidMoves(calculateAllValidMoves(entry.board, nextTurn));

            if (isCheckmate) {
                response.setWinner(currentTurn.toString());
                gameLobbyService.finishGame(gameId, (currentTurn == PieceColor.WHITE ? GameResult.WHITE_WINS : GameResult.BLACK_WINS), GameTermination.CHECKMATE);
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

    private java.util.Map<String, java.util.List<int[]>> calculateAllValidMoves(Board board, PieceColor turn) {
        java.util.Map<String, java.util.List<int[]>> allMoves = new java.util.HashMap<>();
        Spot[][] boxes = board.getBoxes();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Spot start = boxes[r][c];
                Piece piece = start.getPiece();
                if (piece != null && piece.getColor() == turn) {
                    java.util.List<int[]> moves = new ArrayList<>();
                    for (int er = 0; er < 8; er++) {
                        for (int ec = 0; ec < 8; ec++) {
                            Spot end = boxes[er][ec];
                            if (piece.canMove(board, start, end)) {
                                if (!movePutsKingInCheck(board, start, end, turn)) {
                                    moves.add(new int[]{er, ec});
                                }
                            }
                        }
                    }
                    if (!moves.isEmpty()) {
                        allMoves.put(r + "," + c, moves);
                    }
                }
            }
        }
        return allMoves;
    }

    public GameResponse getGameState(String gameId) {
        GameStateCache.Entry entry = gameStateCache.get(gameId)
                .or(() -> gameStateCache.loadFromDb(gameId))
                .orElse(null);
        if (entry == null) return new GameResponse(false, "Game not found", null);

        boolean isCheck = isKingInCheck(entry.board, entry.currentTurn);
        GameResponse response = new GameResponse(true, "State fetched", entry.board);
        response.setCurrentTurn(entry.currentTurn.toString());
        response.setMoveHistory(new ArrayList<>(entry.moveHistory));
        response.setCheck(isCheck);
        if (isCheck) {
            Spot checkedKingSpot = getKingSpot(entry.board, entry.currentTurn);
            if (checkedKingSpot != null) {
                response.setKingRow(checkedKingSpot.getRow());
                response.setKingCol(checkedKingSpot.getCol());
            }
        }
        response.setValidMoves(calculateAllValidMoves(entry.board, entry.currentTurn));
        return response;
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
        Piece piece = start.getPiece();
        Piece captured = end.getPiece();
        Spot eps = board.getEnPassantTarget();
        
        // Handle En Passant simulation
        Piece epCaptured = null;
        Spot epCapturedSpot = null;
        if (piece instanceof Pawn && end == eps) {
            int dir = (color == PieceColor.WHITE) ? 1 : -1;
            epCapturedSpot = board.getSpot(end.getRow() + dir, end.getCol());
            if (epCapturedSpot != null) {
                epCaptured = epCapturedSpot.getPiece();
                epCapturedSpot.setPiece(null);
            }
        }

        // simulate move
        board.movePiece(start, end, false);
        boolean inCheck = isKingInCheck(board, color);
        // undo move
        board.movePiece(end, start, false);
        end.setPiece(captured);
        if (epCapturedSpot != null) {
            epCapturedSpot.setPiece(epCaptured);
        }
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
                    String winnerColor = isWhite ? "BLACK" : "WHITE";
                    String winnerName = isWhite ?
                            (game.getBlackPlayer() != null ? game.getBlackPlayer().getUsername() : "Bot") :
                            (game.getWhitePlayer() != null ? game.getWhitePlayer().getUsername() : "Bot");
                    response.setWinner(winnerColor);
                    response.setMessage("Đấu thủ " + player.getUsername() + " đã đầu hàng. " + 
                                       (winnerColor.equals("WHITE") ? "Quân Trắng" : "Quân Đen") + " (" + winnerName + ") thắng!");
                    gameLobbyService.finishGame(gameId,
                        isWhite ? GameResult.BLACK_WINS : GameResult.WHITE_WINS,
                        GameTermination.RESIGNATION);
                    gameStateCache.evict(gameId);
                    break;
                case "OFFER_DRAW":
                    response.setMessage("Đấu thủ " + player.getUsername() + " muốn cầu hòa.");
                    break;
                case "ACCEPT_DRAW":
                    response.setWinner("DRAW");
                    response.setMessage("Trận đấu hòa vì hai bên đồng ý.");
                    gameLobbyService.finishGame(gameId, GameResult.DRAW, GameTermination.AGREEMENT);
                    gameStateCache.evict(gameId);
                    break;
                case "DECLINE_DRAW":
                    response.setMessage("Đấu thủ " + player.getUsername() + " đã từ chối lời mời cầu hòa.");
                    break;
                case "TIMEOUT":
                    String tWinnerColor = isWhite ? "BLACK" : "WHITE";
                    String tWinnerName = isWhite ?
                            (game.getBlackPlayer() != null ? game.getBlackPlayer().getUsername() : "Bot") :
                            (game.getWhitePlayer() != null ? game.getWhitePlayer().getUsername() : "Bot");
                    response.setWinner(tWinnerColor);
                    response.setMessage("Đấu thủ " + player.getUsername() + " đã hết thời gian. " + 
                                       (tWinnerColor.equals("WHITE") ? "Quân Trắng" : "Quân Đen") + " (" + tWinnerName + ") thắng!");
                    gameLobbyService.finishGame(gameId,
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

    public GameResponse makeBotMove(String gameId, int depth) {
        GameStateCache.Entry entry = gameStateCache.get(gameId).orElse(null);
        if (entry == null) {
            return new GameResponse(false, "Game not found", null);
        }

        String fenForApi = entry.fen;
        if(!fenForApi.contains("")) {
            String turnStr = (entry.currentTurn == PieceColor.WHITE) ? "w" : "b";
            fenForApi += " " + turnStr + " KQkq - 0 1";
        }

        String bestMoveUci = stockfishApiService.getBestMoveFromApi(entry.fen, depth);
        if (bestMoveUci == null) {
            return new GameResponse(false, "Bot đang lag, không nghĩ ra nước đi", null);
        }

        int[] coords = parseUciToMove(bestMoveUci);
        if (coords == null) return new GameResponse(false, "Lỗi dịch tọa độ", null);

        MoveRequest botMove = new MoveRequest();
        botMove.setFromRow(coords[0]);
        botMove.setFromCol(coords[1]);
        botMove.setToRow(coords[2]);
        botMove.setToCol(coords[3]);

        if (bestMoveUci.length() == 5) {
            char promoChar = bestMoveUci.charAt(4);
            String promo = "queen";
            if (promoChar == 'r') promo = "rook";
            else if (promoChar == 'b') promo = "bishop";
            else if (promoChar == 'n') promo = "knight";
            botMove.setPromotion(promo);
        }
        return this.makeMove(gameId, botMove);
    }

    private int[] parseUciToMove(String uciMove) {
        if (uciMove == null || uciMove.length() < 4) return null;
        int fromCol = uciMove.charAt(0) - 'a';
        int toCol = uciMove.charAt(2) - 'a';
        int fromRow = 8 - Character.getNumericValue(uciMove.charAt(1));
        int toRow = 8 - Character.getNumericValue(uciMove.charAt(3));
        return new int[]{fromRow, fromCol, toRow, toCol};
    }
}