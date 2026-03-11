package com.group18.chessgame.service;

import com.group18.chessgame.dto.LoginDTO;
import com.group18.chessgame.dto.RegisterDTO;
import com.group18.chessgame.model.Player;
import com.group18.chessgame.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.group18.chessgame.enums.RegisterResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;

    public RegisterResult register(RegisterDTO dto) {
        if (playerRepository.existsByUsername(dto.getUsername())) {
            return RegisterResult.USERNAME_TAKEN;
        }
        if (playerRepository.existsByEmail(dto.getEmail())) {
            return RegisterResult.EMAIL_TAKEN;
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            return RegisterResult.PASSWORD_MISMATCH;
        }

        Player player = new Player();
        player.setUsername(dto.getUsername());
        player.setEmail(dto.getEmail());
        player.setPassword(dto.getPassword());

        playerRepository.save(player);
        return RegisterResult.SUCCESS;
    }

    public Player login(LoginDTO dto) {
        Player player = playerRepository.findByUsername(dto.getUsername());
        if (player != null && player.getPassword().equals(dto.getPassword())) {
            return player;
        }
        return null;
    }
}
