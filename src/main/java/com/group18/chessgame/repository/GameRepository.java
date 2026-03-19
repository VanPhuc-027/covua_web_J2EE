package com.group18.chessgame.repository;

import com.group18.chessgame.enums.GameStatus;
import com.group18.chessgame.enums.PieceColor;
import com.group18.chessgame.model.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, String> {
    List<Game> findByStatusIn(List<GameStatus> statuses);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query("update Game g set g.currentFen = :fen, g.currentTurn = :turn where g.id = :gameId")
    int updateGameState(@Param("gameId") String gameId, @Param("fen") String fen, @Param("turn") PieceColor turn);

    @Query("SELECT g FROM Game g WHERE (g.whitePlayer.id = :playerId OR g.blackPlayer.id = :playerId) AND g.status = 'FINISHED' ORDER BY g.finishedAt DESC")
    Page<Game> findGameHistory(@Param("playerId") long playerId, Pageable pageable);

    @Query("SELECT COUNT(g) FROM Game g WHERE ((g.whitePlayer.id = :playerId AND g.result = com.group18.chessgame.enums.GameResult.WHITE_WINS) OR (g.blackPlayer.id = :playerId AND g.result = com.group18.chessgame.enums.GameResult.BLACK_WINS))")
    long countWins(@Param("playerId") long playerId);

    @Query("SELECT COUNT(g) FROM Game g WHERE ((g.whitePlayer.id = :playerId AND g.result = com.group18.chessgame.enums.GameResult.BLACK_WINS) OR (g.blackPlayer.id = :playerId AND g.result = com.group18.chessgame.enums.GameResult.WHITE_WINS))")
    long countLosses(@Param("playerId") long playerId);
}
