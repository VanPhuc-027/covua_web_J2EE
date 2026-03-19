package com.group18.chessgame.repository;

import com.group18.chessgame.enums.GameStatus;
import com.group18.chessgame.enums.PieceColor;
import com.group18.chessgame.model.Game;
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
}
