package com.group18.chessgame.controller;

import com.group18.chessgame.model.Player;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    @GetMapping("/profile")
    public String showProfilePage(Model model, HttpSession session) {
        Player user = (Player) session.getAttribute("currentPlayer");
        if (user == null) return "redirect:/login";

        model.addAttribute("currentPlayer", user);
        return "profile";
    }

    @GetMapping("/settings")
    public String showSettingsPage() {
        return "settings";
    }
}
