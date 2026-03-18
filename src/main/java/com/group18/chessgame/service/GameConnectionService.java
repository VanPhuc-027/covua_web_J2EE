package com.group18.chessgame.service;

import com.group18.chessgame.dto.GameResponse;
import com.group18.chessgame.model.Game;
import com.group18.chessgame.model.Player;
import com.group18.chessgame.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class GameConnectionService {

    private final GameLogicService gameLogicService;
    private final GameRepository gameRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private final Map<String, String> sessionToGame = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToUser = new ConcurrentHashMap<>();

    private final Map<String, ScheduledFuture<?>> disconnectTasks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    @EventListener
    public void handleSessionSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String dest = accessor.getDestination();
        String sessionId = accessor.getSessionId();
        
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        Player currentPlayer = sessionAttributes != null ? (Player) sessionAttributes.get("currentPlayer") : null;

        if (dest != null && dest.startsWith("/topic/game/") && currentPlayer != null) {
            String gameId = dest.substring("/topic/game/".length());
            sessionToGame.put(sessionId, gameId);
            sessionToUser.put(sessionId, currentPlayer.getUsername());
            
            // Cancel any pending disconnect task
            String taskKey = gameId + "_" + currentPlayer.getUsername();
            ScheduledFuture<?> pendingTask = disconnectTasks.remove(taskKey);
            if (pendingTask != null) {
                pendingTask.cancel(false);
            }
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        String gameId = sessionToGame.remove(sessionId);
        String username = sessionToUser.remove(sessionId);

        if (gameId != null && username != null) {
            String taskKey = gameId + "_" + username;
            ScheduledFuture<?> task = scheduler.schedule(() -> {
                handleAbandonGame(gameId, username);
                disconnectTasks.remove(taskKey);
            }, 30, TimeUnit.SECONDS);

            disconnectTasks.put(taskKey, task);
        }
    }

    private void handleAbandonGame(String gameId, String abandoningUsername) {
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (gameOpt.isEmpty()) return;
        Game game = gameOpt.get();

        String abandonedPlayerColor = null;
        if (game.getWhitePlayer() != null && game.getWhitePlayer().getUsername().equals(abandoningUsername)) {
            abandonedPlayerColor = "WHITE";
        } else if (game.getBlackPlayer() != null && game.getBlackPlayer().getUsername().equals(abandoningUsername)) {
            abandonedPlayerColor = "BLACK";
        }

        if (abandonedPlayerColor != null) {
            String winner = abandonedPlayerColor.equals("WHITE") ? "BLACK" : "WHITE";
            GameResponse response = new GameResponse(true, "Đối thủ ngắt kết nối. Bạn đã thắng!", null);
            response.setAction("RESIGN"); // or TIMEOUT
            response.setWinner(winner);
            response.setActionPlayer(abandonedPlayerColor);
            response.setMessage("Người chơi " + abandoningUsername + " ngắt kết nối quá 30s. " + (winner.equals("WHITE") ? "Trắng" : "Đen") + " thắng!");
            
            messagingTemplate.convertAndSend("/topic/game/" + gameId, response);
        }
    }
}
