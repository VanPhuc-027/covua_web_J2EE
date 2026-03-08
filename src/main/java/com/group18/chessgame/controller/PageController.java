package com.group18.chessgame.controller;

import com.group18.chessgame.model.Board;
import com.group18.chessgame.service.GameLogicService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private final GameLogicService gameService;

    public PageController(GameLogicService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/")
    public String index(Model model) {

        model.addAttribute("board", gameService.getBoard());

        return "index";
    }
}
