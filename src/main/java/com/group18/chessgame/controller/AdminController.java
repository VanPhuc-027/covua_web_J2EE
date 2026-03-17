package com.group18.chessgame.controller;

import com.group18.chessgame.model.Player;
import com.group18.chessgame.service.PlayerService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final PlayerService playerService;

    @GetMapping("/users")
    public String manageUsers(Model model, HttpSession session) {
        // Tạm thời chưa có logic chặn quyền truy cập, ai có link cũng vào được
        Player currentPlayer = (Player) session.getAttribute("currentPlayer");
        if (currentPlayer == null) {
            return "redirect:/login";
        }
        List<Player> allPlayers = playerService.getAllPlayers();
        model.addAttribute("players", allPlayers);

        return "admin/users";
    }

    @PostMapping("/users/ban")
    public String banUser(@RequestParam Long id, HttpSession session) {
        Player currentPlayer = (Player) session.getAttribute("currentPlayer");
        if (currentPlayer != null && currentPlayer.getId() == id) {
            return "redirect:/admin/users";
        }
        playerService.togglePlayerBan(id);
        return "redirect:/admin/users";
    }
}
