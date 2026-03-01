package com.group18.chessgame.controller;

import com.group18.chessgame.model.Board;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/")
    public String showBoard(Model model) {
        Board board = new Board();
        model.addAttribute("board", board);
        return "index";
    }
}
