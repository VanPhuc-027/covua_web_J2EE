package com.group18.chessgame.config;

import com.group18.chessgame.model.Player;
import com.group18.chessgame.repository.PlayerRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class BanCheckInterceptor implements HandlerInterceptor {
    private final PlayerRepository playerRepository;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentPlayer") != null) {
            Player sessionPlayer = (Player) session.getAttribute("currentPlayer");
            Player dbPlayer = playerRepository.findById(sessionPlayer.getId()).orElse(null);
            if (dbPlayer != null && !dbPlayer.isActive()) {
                session.invalidate();
                response.sendRedirect("/login?error=banned");
                return false;
            }
        }
        return true;
    }
}
