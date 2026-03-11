package com.group18.chessgame.controller;

import com.group18.chessgame.model.Board;
import com.group18.chessgame.service.GameLogicService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession; 

@Controller
public class PageController {

    private final GameLogicService gameService;

    public PageController(GameLogicService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/")
    public String index(Model model) {

        model.addAttribute("board", gameService.getBoard());

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
    @PostMapping("/login")
    public String handleLogin(@RequestParam String username, HttpSession session) {
        session.setAttribute("loggedInUser", username);
        return "redirect:/";
    }
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }
   @PostMapping("/register")
    public String handleRegister(
            @RequestParam String username, 
            @RequestParam String email, 
            @RequestParam String password,
            RedirectAttributes redirectAttributes) {
            System.out.println("Đang đăng ký cho: " + username);
            return "redirect:/login";
    }
    @GetMapping("/profile")
    public String showProfilePage(Model model, HttpSession session) {
        String user = (String) session.getAttribute("loggedInUser");
        model.addAttribute("loggedInUser", user);
        return "profile";
    }
    @GetMapping("/")
    public String showGamePage(Model model, HttpSession session) {
        String user = (String) session.getAttribute("loggedInUser");
        model.addAttribute("loggedInUser", user);
        
        model.addAttribute("board", new Board());
        return "index";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}