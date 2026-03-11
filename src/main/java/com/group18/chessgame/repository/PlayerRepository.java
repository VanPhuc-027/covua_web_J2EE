package com.group18.chessgame.repository;

import com.group18.chessgame.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Player findByUsername(String username);
}
