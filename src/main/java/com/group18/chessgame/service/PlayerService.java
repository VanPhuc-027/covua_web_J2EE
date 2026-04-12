package com.group18.chessgame.service;

import com.group18.chessgame.dto.LoginDTO;
import com.group18.chessgame.dto.RegisterDTO;
import com.group18.chessgame.model.Player;
import com.group18.chessgame.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.group18.chessgame.enums.RegisterResult;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final BCryptPasswordEncoder passwordEncoder;

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
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d).{8,}$";
        if (!dto.getPassword().matches(passwordRegex)) {
            return RegisterResult.PASSWORD_INVALID;
        }

        Player player = new Player();
        player.setUsername(dto.getUsername());
        player.setEmail(dto.getEmail());
        player.setPassword(passwordEncoder.encode(dto.getPassword()));
        playerRepository.save(player);
        return RegisterResult.SUCCESS;
    }

    public Player login(LoginDTO dto) {
        Player player = playerRepository.findByUsername(dto.getUsername());
        if (player != null && passwordEncoder.matches(dto.getPassword(), player.getPassword())) {
            return player;
        }
        return null;
    }

    public List<Player> getTopPlayers() {
        return playerRepository.findTop10ByOrderByEloRatingDesc();
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public void togglePlayerBan(Long id) {
        Player player = playerRepository.findById(id).orElse(null);
        if (player != null) {
            player.setActive(!player.isActive());
            playerRepository.save(player);
        }
    }
}
