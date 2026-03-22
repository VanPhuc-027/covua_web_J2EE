package com.group18.chessgame.repository;

import com.group18.chessgame.model.Move;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MoveRepository extends JpaRepository<Move, Long> {
    List<Move> findByGameIdOrderByMoveOrderAsc(String gameId);
}
