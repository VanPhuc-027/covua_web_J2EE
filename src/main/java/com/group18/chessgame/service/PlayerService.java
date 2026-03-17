package com.group18.chessgame.service;

import com.group18.chessgame.dto.LoginDTO;
import com.group18.chessgame.dto.RegisterDTO;
import com.group18.chessgame.model.Player;
import com.group18.chessgame.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.group18.chessgame.enums.RegisterResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        System.out.println("DEBUG Register - Pass: [" + dto.getPassword() + "] | Confirm: [" + dto.getConfirmPassword() + "]");
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            return RegisterResult.PASSWORD_MISMATCH;
        }
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d).{8,}$";
        if (!dto.getPassword().matches(passwordRegex)) {
            System.out.println("DEBUG: Password " + dto.getPassword() + " KHÔNG khớp Regex!");
            return RegisterResult.PASSWORD_INVALID;
        }

        Player player = new Player();
        player.setUsername(dto.getUsername());
        player.setEmail(dto.getEmail());
        player.setPassword(passwordEncoder.encode(dto.getPassword()));
        playerRepository.save(player);
        System.out.println("DEBUG: Đăng ký thành công cho " + dto.getUsername());
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
}
