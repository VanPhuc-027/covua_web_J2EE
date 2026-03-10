package com.group18.chessgame.service;

import com.group18.chessgame.dto.LoginDTO;
import com.group18.chessgame.dto.RegisterDTO;
import com.group18.chessgame.model.Player;
import org.springframework.stereotype.Service;
import com.group18.chessgame.enums.RegisterResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PlayerService {
    private List<Player> players = new ArrayList<>();
    private long idCounter = 1L;

    public RegisterResult register(RegisterDTO dto) {
        if (players.stream().anyMatch(p -> p.getUsername().equals(dto.getUsername()))) {
            return RegisterResult.USERNAME_TAKEN;
        }
        if (players.stream().anyMatch(p -> p.getEmail().equals(dto.getEmail()))) {
            return RegisterResult.EMAIL_TAKEN;
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            return RegisterResult.PASSWORD_MISMATCH;
        }

        Player player = new Player();
        player.setId(idCounter++);
        player.setUsername(dto.getUsername());
        player.setEmail(dto.getEmail());
        player.setPassword(dto.getPassword());
        player.setEloRating(1200);
        player.setCreatedAt(LocalDateTime.now());

        players.add(player);
        return RegisterResult.SUCCESS;
    }

    public Player login(LoginDTO dto) {
        return players.stream()
                .filter(p -> p.getUsername().equals(dto.getUsername())
                        && p.getPassword().equals(dto.getPassword()))
                .findFirst()
                .orElse(null);
    }
}
