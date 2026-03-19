package com.group18.chessgame.service;

import com.group18.chessgame.enums.PieceColor;
import com.group18.chessgame.model.Board;
import com.group18.chessgame.model.Game;
import com.group18.chessgame.repository.GameRepository;
import com.group18.chessgame.utils.FenUtils;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * In-memory cache for active games to avoid round-trips to remote DB (Railway) on every click.
 * Keeps deploy structure unchanged (single Spring Boot service), and gracefully falls back to DB on cache miss.
 */
@Component
public class GameStateCache {

    private static final long TTL_MS = Duration.ofMinutes(10).toMillis();

    private final GameRepository gameRepository;
    private final ConcurrentHashMap<String, Entry> cache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    public GameStateCache(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public ReentrantLock lockFor(String gameId) {
        return locks.computeIfAbsent(gameId, k -> new ReentrantLock());
    }

    public Optional<Entry> get(String gameId) {
        Entry e = cache.get(gameId);
        if (e == null) return Optional.empty();
        if (isExpired(e)) {
            cache.remove(gameId, e);
            return Optional.empty();
        }
        e.lastAccessMs = System.currentTimeMillis();
        return Optional.of(e);
    }

    public Optional<Entry> loadFromDb(String gameId) {
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) return Optional.empty();

        Board board = new Board();
        FenUtils.fenToBoard(game.getCurrentFen(), board);

        Entry entry = new Entry(gameId, game.getCurrentFen(), game.getCurrentTurn(), board);
        cache.put(gameId, entry);
        return Optional.of(entry);
    }

    public void put(String gameId, String fen, PieceColor currentTurn, Board board) {
        cache.put(gameId, new Entry(gameId, fen, currentTurn, board));
    }

    private boolean isExpired(Entry e) {
        return System.currentTimeMillis() - e.lastAccessMs > TTL_MS;
    }

    /** Xóa game khỏi cache khi trận đấu kết thúc */
    public void evict(String gameId) {
        cache.remove(gameId);
        locks.remove(gameId);
    }

    public static final class Entry {
        public final String gameId;
        public String fen;
        public PieceColor currentTurn;
        public Board board;
        public volatile long lastAccessMs;

        public Entry(String gameId, String fen, PieceColor currentTurn, Board board) {
            this.gameId = gameId;
            this.fen = fen;
            this.currentTurn = currentTurn;
            this.board = board;
            this.lastAccessMs = System.currentTimeMillis();
        }
    }
}

