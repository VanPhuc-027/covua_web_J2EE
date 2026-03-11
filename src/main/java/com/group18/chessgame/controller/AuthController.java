package com.group18.chessgame.controller;

import com.group18.chessgame.dto.LoginDTO;
import com.group18.chessgame.dto.RegisterDTO;
import com.group18.chessgame.model.Player;
import com.group18.chessgame.service.GameService;
import com.group18.chessgame.service.PlayerService;
import com.group18.chessgame.enums.RegisterResult;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final PlayerService playerService;
    private final GameService gameService;

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute RegisterDTO registerDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) return "register";

        RegisterResult result = playerService.register(registerDTO);
        switch (result) {
            case USERNAME_TAKEN -> { model.addAttribute("error", "Username đã được sử dụng!"); return "register"; }
            case EMAIL_TAKEN -> { model.addAttribute("error", "Email đã được sử dụng!"); return "register"; }
            case PASSWORD_MISMATCH -> { model.addAttribute("error", "Mật khẩu xác nhận không khớp!"); return "register"; }
            case SUCCESS -> { return "redirect:/login?registered=true"; }
        }
        return "register";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model, @RequestParam(required = false) String registered, HttpSession session) {
        session.setAttribute("init_session", true);
        model.addAttribute("loginDTO", new LoginDTO());
        if (registered != null) model.addAttribute("message", "Đăng ký thành công! Vui lòng đăng nhập.");
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@Valid @ModelAttribute LoginDTO loginDTO, BindingResult bindingResult, Model model, HttpSession session) {
        if (bindingResult.hasErrors()) return "login";

        Player player = playerService.login(loginDTO);
        if (player == null) {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
            return "login";
        }

        session.setAttribute("currentPlayer", player);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/")
    public String showLobbyPage(Model model, HttpSession session) {
        Player user = (Player) session.getAttribute("currentPlayer");
        if (user == null) return "redirect:/login";

        model.addAttribute("currentPlayer", user);
        model.addAttribute("waitingGames", gameService.getWaitingGame());
        return "index";
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model, HttpSession session) {
        Player user = (Player) session.getAttribute("currentPlayer");
        if (user == null) return "redirect:/login";

        model.addAttribute("currentPlayer", user);
        return "profile";
    }
}