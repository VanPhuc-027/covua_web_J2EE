package com.group18.chessgame.repository;

import com.group18.chessgame.enums.GameStatus;
import com.group18.chessgame.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, String> {
    List<Game> findByStatus(GameStatus status);
}
