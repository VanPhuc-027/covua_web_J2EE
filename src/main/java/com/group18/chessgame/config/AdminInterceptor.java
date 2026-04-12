package com.group18.chessgame.config;

import com.group18.chessgame.model.Player;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Player currentPlayer = (Player) session.getAttribute("currentPlayer");
        if (currentPlayer == null || !"ROLE_ADMIN".equals(currentPlayer.getRole())) {
            response.sendRedirect("/");
            return false;
        }
        return true;
    }
}
