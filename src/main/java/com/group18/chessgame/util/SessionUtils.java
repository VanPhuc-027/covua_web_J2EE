package com.group18.chessgame.util;

import com.group18.chessgame.model.Player;
import jakarta.servlet.http.HttpSession;

public class SessionUtils {
    public static Player getCurrentPlayer(HttpSession session) {
        return (Player) session.getAttribute("currentPlayer");
    }
    public static boolean isLoggedIn(HttpSession session) {
        return getCurrentPlayer(session) != null;
    }
}
