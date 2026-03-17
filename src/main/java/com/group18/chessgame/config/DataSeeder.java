package com.group18.chessgame.config;

import com.group18.chessgame.model.Player;
import com.group18.chessgame.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final PlayerRepository playerRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!playerRepository.existsByUsername("admin")) {
            Player admin = new Player();
            admin.setUsername("admin");
            admin.setEmail("adminK@gmail.com");
            admin.setPassword(passwordEncoder.encode("Admin@033830"));
            admin.setRole("ROLE_ADMIN");
            admin.setActive(true);
            admin.setEloRating(9999);
            admin.setCreatedAt(LocalDateTime.now());
            playerRepository.save(admin);
            System.out.println("=================================================");
            System.out.println("✅ Đã khởi tạo thành công tài khoản ADMIN!");
            System.out.println("👉 Username: admin");
            System.out.println("👉 Password: Admin@033830");
            System.out.println("=================================================");
        }
    }
}
